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
            content = content.replaceFirst("${tags.includeOpen}(.+?)${tags.includeClose}", include)
        }
        // Parse 'Include' - END

        // Parse 'If'
        val patternIf = Pattern.compile("${tags.ifOpen}(.+?)${tags.ifClose}(.+?)${tags.endIf}")
        val matcherIf = patternIf.matcher(content)
        while (matcherIf.find()) {
            val g1 = matcherIf.group(1)
            val g2 = matcherIf.group(2)
            val ctx = g1.trim()
            val result = resolveIf(ctx)
            val replace = "${tags.ifOpen}$g1${tags.ifClose}$g2${tags.endIf}"
            if (result) {
                content = content.replaceFirst(replace, g2)
            } else {
                if (g2.contains(tags.elseTag)) {
                    val replaceWith = g2.substring(g2.indexOf(tags.elseTag) + tags.elseTag.length, g2.length)
                    logger.w("", "-> $g2")
                    logger.c("", "-> $replaceWith")
                    content = content.replaceFirst(replace, "- - - =")
                } else {
                    content = content.replaceFirst(replace, "")
                }
            }
        }
        // Parse 'If' - END

        // TODO: Refactor so performance are gained.

//        rows.forEachIndexed {
//            index, line ->
//            var row = line
//
//            // Trim comments that are not at the line start
//            val lineCommentIndex = row.indexOf(tags.lineComment)
//            if (!row.startsWith(tags.lineComment) && lineCommentIndex > 0) {
//                row = row.substring(0, lineCommentIndex)
//            }
//            rows[index] = row
//
//            // Trim comments that are at line start:
//            if (row.startsWith(tags.lineComment)) {
//                row = ""
//                rows[index] = row
//                rowsToBeIgnored.add(index)
//            }
//
//            // Trim tab placeholder
//            row = row.replace(tags.tabPlaceholder, "")
//            rows[index] = row
//
//            // Parse <include> tags
//            val pInclude = Pattern.compile("${tags.includeOpen}(.+?)${tags.includeClose}")
//            val mInclude = pInclude.matcher(line)
//            while (mInclude.find()) {
//                val include = mInclude.group(1).trim()
//                var element = decorate(include, data)
//                if (element.endsWith("\n")) {
//                    element = element.substring(0, element.lastIndex)
//                }
//                row = row.replace(mInclude.group(0), element)
//                rows[index] = row
//            }
//        }
//
//        val cleanRows = mutableListOf<String>()
//        rows.forEachIndexed{
//            index, row ->
//            if(!rowsToBeIgnored.contains(index)){
//                cleanRows.add(row)
//            }
//        }
//        rows.clear()
//        rows.addAll(cleanRows)
//        cleanRows.clear()
//        rowsToBeIgnored.clear()
//
//        rows.forEachIndexed {
//            index, line ->
//            var row = line
//            // Parse <foreach>
//            val pFor = Pattern.compile("${tags.foreachOpen}(.+?)${tags.foreachClose}")
//            val mFor = pFor.matcher(line)
//            while (mFor.find()) {
//                val forCondition = mFor.group(1).trim()
//                row = row.replace(mFor.group(0), "")
//                rows[index] = row
//                if (!row.isEmpty()) {
//                    throw IllegalStateException(Messages.CONTENT_AFTER_FOR_OPENING(template))
//                }
//                if (foreachState != null) {
//                    throw IllegalStateException(Messages.FOR_NOT_CLOSED(template))
//                } else {
//                    foreachState = ForeachState(index, -1, forCondition)
//                }
//            }
//
//            // Parse <endfor/>
//            val pEndfor = Pattern.compile(tags.endFor)
//            val mEndfor = pEndfor.matcher(line)
//            while (mEndfor.find()) {
//                row = row.replace(mEndfor.group(0), "")
//                if (row.isEmpty()) {
//                    rowsToBeIgnored.add(index)
//                }
//                rows[index] = row
//                if (foreachState == null) {
//                    throw IllegalStateException(Messages.FOR_NOT_OPENED(template))
//                } else {
//                    foreachState?.to = index
//                    foreachStates.add(foreachState)
//                    foreachState = null
//                }
//            }
//
//            var foreachStateInProgress = false
//            val currentState = foreachState
//            currentState?.let {
//                foreachStateInProgress = index > currentState.from
//            }
//            if (foreachStateInProgress) {
//                foreachTemplates[index] = line
//                rowsToBeIgnored.add(index)
//            }
//        }
//
//        rows.forEachIndexed {
//            index, line ->
//            var row = line
//            val state = getForeachState(foreachStates, index)
//            state?.let {
//                if (state.from == index) {
//                    val templateRows = mutableListOf<String>()
//                    for (x in state.from..state.to) {
//                        foreachTemplates[x]?.let {
//                            item ->
//                            templateRows.add(item)
//                        }
//                    }
//                    row = resolveForeach(template, templateRows, data, state.value)
//                }
//            }
//            rows[index] = row
//        }
//
//        rows.forEachIndexed {
//            index, line ->
//            if (!rowsToBeIgnored.contains(index)) {
//                var row = line
//                if (row.indexOf(tags.ifOpen) >= 0 && row.indexOf(tags.ifClose) < 0) {
//                    throw IllegalStateException(Messages.IF_CONDITION_NOT_CLOSED(template))
//                }
//
//                if (row.indexOf(tags.ifClose) >= 0 && row.indexOf(tags.ifOpen) < 0) {
//                    throw IllegalStateException(Messages.IF_CONDITION_NOT_OPENED(template))
//                }
//
//                // Parse <if> tags
//                var endIfDetected: Boolean
//                val pIf = Pattern.compile("${tags.ifOpen}(.+?)${tags.ifClose}")
//                val mIf = pIf.matcher(row)
//                while (mIf.find()) {
//                    endIfDetected = false
//                    val ifStartPos = row.indexOf(tags.ifOpen)
//                    var elseStartPos = row.indexOf(tags.elseTag)
//                    var endIfStartPos = row.indexOf(tags.endIf)
//
//                    val ifCondition = mIf.group(1).trim()
//                    val result = resolveIf(template, data, ifCondition)
//                    row = row.replaceFirst(mIf.group(0), "")
//                    if (row.isEmpty()) {
//                        rowsToBeIgnored.add(index)
//                    }
//                    rows[index] = row
//                    val ifState = IfState(index, -1, result)
//                    ifStates.add(ifState)
//                    ifStatesOpened++
//
//                    val pElse = Pattern.compile(tags.elseTag)
//                    val mElse = pElse.matcher(row)
//                    if (elseStartPos < endIfStartPos && mElse.find()) {
//                        elseStartPos = row.indexOf(tags.elseTag)
//                        row = row.replaceFirst(mElse.group(0), "")
//                        if (row.isEmpty()) {
//                            rowsToBeIgnored.remove(index)
//                        }
//                        rows[index] = row
//                        endIfStartPos = row.indexOf(tags.endIf)
//                        val pEndIf = Pattern.compile(tags.endIf)
//                        val mEndIf = pEndIf.matcher(row)
//                        if (mEndIf.find()) {
//                            endIfDetected = true
//                            if (ifStates.contains(ifState)) {
//                                ifStates.remove(ifState)
//                                ifStatesOpened--
//                            }
//
//                            if (result) {
//                                row = row.replaceFirst(row.substring(elseStartPos, endIfStartPos), "")
//                            } else {
//                                row = row.replaceFirst(row.substring(ifStartPos, mElse.start()), "")
//                            }
//                            row = row.replaceFirst(mEndIf.group(0), "")
//                            if (row.isEmpty()) {
//                                rowsToBeIgnored.remove(index)
//                            }
//                            rows[index] = row
//                        }
//                    }
//
//                    if (!endIfDetected) {
//                        val pEndIf = Pattern.compile(tags.endIf)
//                        val mEndIf = pEndIf.matcher(row)
//                        if (mEndIf.find()) {
//                            if (ifStates.contains(ifState)) {
//                                ifStates.remove(ifState)
//                                ifStatesOpened--
//                            }
//
//                            val endIfStart = mEndIf.start()
//                            if (!result) {
//                                row = row.replaceFirst(row.substring(0, endIfStart), "")
//                            }
//                            row = row.replaceFirst(mEndIf.group(0), "")
//                            if (row.isEmpty()) {
//                                rowsToBeIgnored.remove(index)
//                            }
//                            rows[index] = row
//                        } else if (elseStartPos >= 0) {
//                            throw IllegalStateException(Messages.IF_NOT_CLOSED(template))
//                        }
//                    }
//                }
//
//                // Parse <else> tags
//                val pElse = Pattern.compile(tags.elseTag)
//                val mElse = pElse.matcher(row)
//                while (mElse.find()) {
//                    row = row.replaceFirst(mElse.group(0), "")
//                    if (row.trim().isEmpty()) {
//                        rowsToBeIgnored.add(index)
//                    } else {
//                        throw IllegalStateException(Messages.ELSE_VERTICAL_INVALID(template))
//                    }
//                    rows[index] = row
//                    var ifState: IfState? = null
//                    if (!ifStates.isEmpty() && ifStatesOpened > 0) {
//                        ifState = ifStates[ifStates.size - ifStatesOpened]
//                    }
//                    if (ifState != null) {
//                        val elseState = ElseState(index, -1)
//                        elseStates.add(elseState)
//                        elseStatesOpened++
//                    }
//                }
//
//                // Parse <endif/> tag
//                val pEndIf = Pattern.compile(tags.endIf)
//                val mEndIf = pEndIf.matcher(row)
//                while (mEndIf.find()) {
//                    if (ifStatesOpened == 0) {
//                        throw IllegalStateException(Messages.IF_NOT_OPENED(template))
//                    }
//
//                    row = row.replaceFirst(mEndIf.group(0), "")
//                    if (row.isEmpty()) {
//                        rowsToBeIgnored.add(index)
//                    }
//                    rows[index] = row
//                    var ifState: IfState? = null
//                    if (!ifStates.isEmpty() && ifStatesOpened > 0) {
//                        ifState = ifStates[ifStates.size - ifStatesOpened]
//                    }
//                    if (ifState != null) {
//                        ifState.to = index
//                        ifStatesOpened--
//                    }
//                    if (!elseStates.isEmpty() && elseStatesOpened > 0) {
//                        val elseState = elseStates[elseStates.size - elseStatesOpened]
//                        if (elseState != null) {
//                            elseState.to = index
//                            elseStatesOpened--
//                        }
//                    }
//                }
//
//                // Parse <dc> tags
//                val p = Pattern.compile("${tags.open}(.+?)${tags.close}")
//                val m = p.matcher(row)
//                val commands = mutableListOf<String>()
//                while (m.find()) {
//                    val result = m.group().trim()
//                    commands.add(result)
//                }
//                if (!commands.isEmpty()) {
//                    decoratedRows[index] = commands
//                }
//            }
//        }
//
//        rows.forEachIndexed {
//            index, line ->
//            val isLineValid: Boolean
//            val satisfiesElse = satisfiesElse(elseStates, index, template)
//            if (!satisfiesIf(ifStates, index, template)) {
//                isLineValid = satisfiesElse
//            } else {
//                isLineValid = !satisfiesElse
//            }
//            if (!rowsToBeIgnored.contains(index) && isLineValid) {
//                var renderedLine = line
//                if (decoratedRows.containsKey(index)) {
//                    decoratedRows[index]?.forEach {
//                        row ->
//                        val decoration = row
//                                .replace(tags.open, "")
//                                .replace(tags.close, "")
//                                .trim()
//                        val result = resolve(template, data, decoration)
//                        renderedLine = renderedLine.replace(row, result)
//                    }
//                }
//                rendered.append("$renderedLine\n")
//            }
//        }

        return content
                .replace(tags.newLine, "\n")
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
