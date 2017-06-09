package net.milosvasic.decorator

import net.milosvasic.decorator.content.Messages
import net.milosvasic.decorator.data.Collection
import net.milosvasic.decorator.data.Data
import net.milosvasic.decorator.data.TemplateData
import net.milosvasic.decorator.data.Value
import net.milosvasic.decorator.separator.Separator
import net.milosvasic.decorator.template.Template
import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration
import net.milosvasic.tautology.Tautology
import net.milosvasic.tautology.expression.ExpressionValue
import net.milosvasic.tautology.parser.TautologyParser
import net.milosvasic.tautology.parser.TautologyParserDelegate
import java.lang.IllegalStateException
import java.util.regex.Pattern

class Decorator(template: String, data: Data) : Template(template, data) {

    override val tags = DecoratorTags()
    override val templateExtension = "decoration"
    override val memberSeparator = Separator.MEMBER()
    override val templateMainClass = DecoratorTemplateClass()

    private val tautology = Tautology()
    private val keyCacheIncludes = mutableMapOf<String, String>()
    private val keyCacheData = mutableMapOf<String, TemplateData?>()
    private val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))

    init {
        data.content.putAll(templateMainClass.data.content)
    }

    override fun getContent(): String {
        val templateFile = javaClass.classLoader.getResource("$template.$templateExtension")
        var content = templateFile.readText()
                .replace(Regex("${tags.multiLineCommentOpen}(?:.|[\\n\\r])*?${tags.multiLineCommentClose}"), "") // Clean up multiline comments
                .replace(Regex("${tags.lineComment}.*"), "") // Clean up single line comments
                .replace(Regex("(?m)^[ \t]*\r?\n"), "") // Clean up empty lines
                .replace("\n", tags.newLine)

        // Parse 'For'
        val patternFor = Pattern.compile("${tags.foreachOpen}(.+?)${tags.foreachClose}(.+?)(${tags.endFor})")
        val matcherFor = patternFor.matcher(content)
        while (matcherFor.find()) {
            val g1 = matcherFor.group(1)
            val g2 = matcherFor.group(2)
            val g3 = matcherFor.group(3)
            val ctx = g1.trim()
            val data = getData(ctx)
            if (data is Collection) {
                val count = data.items.count()
                val replaceWith = g2.repeat(count)
                content = content.replaceFirst("${tags.foreachOpen}$g1${tags.foreachClose}$g2$g3", replaceWith)
            } else throw IllegalStateException(Messages.ONLY_COLLECTION_ALLOWED(template))
        }
        // Parse 'For' - END

        // Parse 'Include'
        val patternInclude = Pattern.compile("${tags.includeOpen}(.+?)${tags.includeClose}")
        val matcherInclude = patternInclude.matcher(content)
        while (matcherInclude.find()) {
            val g1 = matcherInclude.group(1)
            val ctx = g1.trim()
            var include = keyCacheIncludes[ctx]
            if (include == null) {
                val decorator = Decorator(ctx, data)
                include = decorator.getContent()
                keyCacheIncludes[ctx] = include
            }
            content = content.replaceFirst("${tags.includeOpen}$g1${tags.includeClose}", include)
        }
        // Parse 'Include' - END


        var ifIndex = 0
        var lastIfIndex = 0
        val patternTag = Pattern.compile("(<.+?>)")
        var matcherTag = patternTag.matcher(content)
        while (matcherTag.find()) {
            val g1 = matcherTag.group(1)
            val start = matcherTag.start(1)
            val toReplace = content.substring(start, start + g1.length)
            logger.v("", "-> $toReplace")
            when (g1) {
                tags.ifOpen -> {
                    ifIndex++
                    lastIfIndex = ifIndex
                    if (ifIndex > 1) {
                        logger.i("", "-> $start")

//                        val replaced = toReplace
//                                .replaceFirst(tags.ifOpen, "<if_$ifIndex>")
//                                .replaceFirst(tags.ifClose, "</if_$ifIndex>")
//
//                        content = StringBuilder()
//                                .append(content.substring(0, start))
//                                .append(replaced)
//                                .toString()
                    }
                }
                tags.ifClose -> {
                    logger.w("", "-> $start")
                }
                tags.endIf -> {
                    if (ifIndex > 1) {
                        logger.d("", "-> $start")

//                        val toReplace = content.substring(start, content.length)
//                        val replaced = toReplace.replaceFirst(tags.endIf, "<endif_$ifIndex/>")
//
//                        content = StringBuilder()
//                                .append(content.substring(0, start))
//                                .append(replaced)
//                                .toString()
                    }
                    ifIndex--
                }
            }
        }

        logger.n("", "-> $ifIndex $lastIfIndex")

        // Parse 'If'
//        val patternIf = Pattern.compile("${tags.ifOpen}(.+?)${tags.ifClose}(.+?)${tags.endIf}")
//        var matcherIf = patternIf.matcher(content)
//        while (matcherIf.find()) {
//            val g1 = matcherIf.group(1)
//            val g2 = matcherIf.group(2)
//            val ctx = g1.trim()
//            val result = resolveIf(ctx)
//            val replace = "${tags.ifOpen}$g1${tags.ifClose}$g2${tags.endIf}"
//            if (result) {
//                if (g2.contains(tags.elseTag)) {
//                    val replaceWith = g2.substring(0, g2.indexOf(tags.elseTag))
//                    content = content.replaceFirst(replace, replaceWith)
//                } else {
//                    content = content.replaceFirst(replace, g2)
//                }
//            } else {
//                if (g2.contains(tags.elseTag)) {
//                    // logger.c("", "-> $g2")
//                    val replaceWith = g2.substring(g2.indexOf(tags.elseTag) + tags.elseTag.length, g2.length)
//                    // logger.c("", "-> $replaceWith")
//                    // logger.c("", "-> $replace")
//                    content = content.replaceFirst(replace, replaceWith)
//                } else {
//                    content = content.replaceFirst(replace, "")
//                }
//            }
//            matcherIf = patternIf.matcher(content)
//        }
        // Parse 'If' - END

        // Parse data tags
        val patternData = Pattern.compile("${tags.open}(.+?)${tags.close}")
        val matcherData = patternData.matcher(content)
        while (matcherData.find()) {
            val g1 = matcherData.group(1)
            val ctx = g1.trim()
            val value = resolve(ctx)
            content = content.replaceFirst("${tags.open}$g1${tags.close}", value)
        }
        // Parse data tags - END

        return content
                .replace(tags.newLine, "\n")
                .replace(tags.tabPlaceholder, "")
                .replace(Regex("(?m)^[ \t]*\r?\n"), "") // Clean up empty lines
    }

    private fun getData(key: String): TemplateData? {
        var tdata = keyCacheData[key]
        if (tdata == null) {
            val it = key.trim().split(memberSeparator.value).iterator()
            if (it.hasNext()) {
                tdata = data.content[it.next()]
            }
            while (tdata != null && it.hasNext()) {
                when (tdata) {
                    is Data -> {
                        val param = it.next()
                        tdata = tdata.content[param]
                    }
                }
            }
            if (tdata != null) {
                keyCacheData[key] = tdata
            }
        }
        return tdata
    }

    private fun resolve(key: String): String {
        val data = getData(key)
        if (data != null && data is Value) {
            return data.content
        } else {
            if (data is Collection) {
                throw IllegalArgumentException(Messages.COLLECTION_NOT_ALLOWED(key, template))
            } else {
                throw IllegalArgumentException(Messages.COULD_NOT_RESOLVE(key, template))
            }
        }
    }

    private fun resolveIf(key: String): Boolean {
        val delegate = object : TautologyParserDelegate {
            override fun getExpressionValue(key: String): ExpressionValue? {
                val resolve: String
                try {
                    resolve = resolve(key)
                } catch (e: Exception) {
                    return null
                }
                return object : ExpressionValue {
                    override fun getValue(): Boolean {
                        return !resolve.isEmpty()
                    }
                }
            }
        }
        val parser = TautologyParser(delegate)
        val expressions = parser.parse(key)
        return tautology.evaluate(expressions)
    }

}
