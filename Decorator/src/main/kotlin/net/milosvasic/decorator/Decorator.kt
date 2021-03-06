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
    override val arrayOpenSeparator = Separator.ARRAY_OPEN()
    override val arrayCloseSeparator = Separator.ARRAY_CLOSE()
    override val templateMainClass = DecoratorTemplateClass()

    private val tautology = Tautology()
    private val keyCacheIncludes = mutableMapOf<String, String>()
    private val keyCacheData = mutableMapOf<String, TemplateData?>()
    private val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))

    init {
        data.content.putAll(templateMainClass.data.content)
    }

    override fun getContent(): String {
        val templateFile = javaClass.classLoader.getResource("$template.$templateExtension") ?: throw IllegalArgumentException(Messages.TEMPLATE_DOES_NOT_EXIST(template))
        var content = templateFile.readText()
                .replace(Regex("${tags.multiLineCommentOpen}(?:.|[\\n\\r])*?${tags.multiLineCommentClose}"), "") // Clean up multiline comments
                .replace(Regex("${tags.lineComment}.*"), "") // Clean up single line comments
                .replace(Regex("(?m)^[ \t]*\r?\n"), "") // Clean up empty lines
                .replace("\n", tags.newLine)
                .replace(tags.ifClose, "${tags.ifClose}${tags.tabPlaceholder}") // Preventing regex pass ok
                .replace(tags.elseTag, "${tags.elseTag}${tags.tabPlaceholder}")

        // Parse 'For'
        val patternFor = Pattern.compile("${tags.foreachOpen}(.+?)${tags.foreachClose}(.+?)${tags.endFor}")
        val matcherFor = patternFor.matcher(content)
        while (matcherFor.find()) {
            val g1 = matcherFor.group(1)
            val g2 = matcherFor.group(2)
            val ctx = g1.trim()
            val data = getData(ctx)
            if (data is Collection) {
                val count = data.items.count()
                val replaceWith = StringBuilder()
                for (x in 0..count - 1) {
                    var replaced = g2
                    replaced = replaced.replace(tags.indexTag, x.toString())
                    replaced = replaced.replace(tags.itemTag, "$ctx[$x]")
                    replaceWith.append(replaced)
                }
                content = content.replaceFirst(
                        "${tags.foreachOpen}$g1${tags.foreachClose}$g2${tags.endFor}",
                        replaceWith.toString()
                )
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

        // Mark nested 'If's
        var ifIndex = 0
        var highestIfIndex = 0
        var replacementDelta = 0
        val patternTag = Pattern.compile("(${tags.ifOpen}|${tags.ifClose}|${tags.endIf})")
        val matcherTag = patternTag.matcher(content)
        while (matcherTag.find()) {
            val g1 = matcherTag.group(1)
            val start = matcherTag.start(1) - replacementDelta

            fun replace() {
                var delta = g1.length
                var endTag = tags.tagEnd
                if (g1.endsWith(tags.tagClosedEnd)) {
                    endTag = tags.tagClosedEnd
                }
                val g1Replaced = g1.replace(endTag, "_$ifIndex$endTag")
                val replaced = StringBuilder()
                        .append(content.substring(0, start))
                        .append(g1Replaced)
                        .append(content.substring(start + g1.length, content.length))
                        .toString()
                delta = g1Replaced.length - delta
                replacementDelta -= delta
                content = replaced
            }

            when (g1) {
                tags.ifOpen -> {
                    ifIndex++
                    if (ifIndex > highestIfIndex) {
                        highestIfIndex = ifIndex
                    }
                    if (ifIndex > 1) {
                        replace()
                    }
                }
                tags.ifClose -> {
                    if (ifIndex > 1) {
                        replace()
                    }
                }
                tags.endIf -> {
                    if (ifIndex > 1) {
                        replace()
                    }
                    ifIndex--
                }
            }
        }
        // Mark nested 'If's - END.

        // Parse 'If'
        for (x in highestIfIndex downTo 1) {
            val pattern: String
            if (x == 1) {
                pattern = "${tags.ifOpen}(.+?)${tags.ifClose}(.+?)${tags.endIf}"
            } else {
                pattern = "${tags.tagStart}if_$x${tags.tagEnd}(.+?)${tags.tagClosedStart}if_$x${tags.tagEnd}(.+?)${tags.tagStart}endif_$x${tags.tagClosedEnd}"
            }
            val patternIf = Pattern.compile(pattern)
            var matcherIf = patternIf.matcher(content)
            while (matcherIf.find()) {
                val g1 = matcherIf.group(1)
                val g2 = matcherIf.group(2)
                val ctx = g1.trim()
                val result = resolveIf(ctx)
                val replace: String
                if (x == 1) {
                    replace = "${tags.ifOpen}$g1${tags.ifClose}$g2${tags.endIf}"
                } else {
                    replace = "${tags.tagStart}if_$x${tags.tagEnd}$g1${tags.tagClosedStart}if_$x${tags.tagEnd}$g2${tags.tagStart}endif_$x${tags.tagClosedEnd}"
                }
                if (result) {
                    if (g2.contains(tags.elseTag)) {
                        val replaceWith = g2.substring(0, g2.indexOf(tags.elseTag))
                        content = content.replaceFirst(replace, replaceWith)
                    } else {
                        content = content.replaceFirst(replace, g2)
                    }
                } else {
                    if (g2.contains(tags.elseTag)) {
                        val replaceWith = g2.substring(g2.indexOf(tags.elseTag) + tags.elseTag.length, g2.length)
                        content = content.replaceFirst(replace, replaceWith)
                    } else {
                        content = content.replaceFirst(replace, "")
                    }
                }
                matcherIf = patternIf.matcher(content)
            }
        }
        // Parse 'If' - END

        // Parse data tags
        val patternData = Pattern.compile("${tags.open}(.+?)${tags.close}")
        val matcherData = patternData.matcher(content)
        while (matcherData.find()) {
            val g1 = matcherData.group(1)
            val ctx = g1.trim()
            val value = resolve(ctx)
            content = content.replaceFirst("${tags.open}$g1${tags.close}", value ?: "")
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
            tdata = data
            val it = key.trim().split(memberSeparator.value).iterator()
            while (it.hasNext()) {
                var matched = false
                val param = it.next()
                val pattern = "(.+?)\\${arrayOpenSeparator.value}(.*?)\\${arrayCloseSeparator.value}"
                val patternArrayAccess = Pattern.compile(pattern)
                val matcherArrayAccess = patternArrayAccess.matcher(param)
                while (matcherArrayAccess.find()) {
                    matched = true
                    val g1 = matcherArrayAccess.group(1)
                    val g2 = matcherArrayAccess.group(2)
                    if (tdata is Data) {
                        val arrayData = tdata.content[g1]
                        if (arrayData is Collection) {
                            try {
                                tdata = arrayData.items[g2.toInt()]
                            } catch (e: IndexOutOfBoundsException) {
                                throw IllegalArgumentException(
                                        Messages.INVALID_INDEX(param, arrayData.items.size)
                                )
                            }
                        }
                    }
                }
                if (!matched) {
                    when (tdata) {
                        is Data -> {
                            tdata = tdata.content[param]
                        }
                    }
                }
            }
            if (tdata != null) {
                keyCacheData[key] = tdata
            }
        }
        return tdata
    }

    private fun resolve(key: String): String? {
        val data = getData(key)
        if (data != null && data is Value) {
            return data.content
        } else {
            if (data is Collection) {
                throw IllegalArgumentException(Messages.COLLECTION_NOT_ALLOWED(key, template))
            } else {
                return null
            }
        }
    }

    private fun resolveIf(key: String): Boolean {
        val delegate = object : TautologyParserDelegate {
            override fun getExpressionValue(key: String): ExpressionValue? {
                val resolve: String?
                try {
                    resolve = resolve(key)
                } catch (e: Exception) {
                    return null
                }
                if (resolve != null) {
                    return object : ExpressionValue {
                        override fun getValue(): Boolean {
                            return !resolve.isEmpty()
                        }
                    }
                }
                return resolve
            }
        }
        try {
            val parser = TautologyParser(delegate)
            val expressions = parser.parse(key)
            return tautology.evaluate(expressions)
        } catch (e: Exception) {
            return false
        }
    }

}
