package net.milosvasic.decorator

import net.milosvasic.decorator.content.Messages
import net.milosvasic.decorator.data.Collection
import net.milosvasic.decorator.data.Data
import net.milosvasic.decorator.data.TemplateData
import net.milosvasic.decorator.data.Value
import net.milosvasic.decorator.data.state.ElseState
import net.milosvasic.decorator.data.state.ForeachState
import net.milosvasic.decorator.data.state.IfState
import net.milosvasic.decorator.separator.Separator
import net.milosvasic.decorator.template.TemplateSystem
import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration
import net.milosvasic.tautology.Tautology
import net.milosvasic.tautology.expression.ExpressionValue
import net.milosvasic.tautology.parser.TautologyParser
import net.milosvasic.tautology.parser.TautologyParserDelegate
import java.lang.IllegalStateException
import java.util.regex.Pattern

class Decorator : TemplateSystem {

    override val tags = DecoratorTags()
    override val templateExtension = "decoration"
    override val memberSeparator = Separator.MEMBER()
    override val templateMainClass = DecoratorTemplateClass()

    private val tautology = Tautology()
    private val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))

    override fun decorate(template: String, data: Data): String {
        val templateFile = javaClass.classLoader.getResource("$template.$templateExtension")
        val rendered = StringBuilder()
        val content = templateFile.readText()
        val rows = mutableListOf<String>()
        rows.addAll(content.split("\n"))
        var ifState: IfState? = null
        val ifStates = mutableListOf<IfState?>()
        var elseState: ElseState? = null
        val elseStates = mutableListOf<ElseState?>()
        var foreachState: ForeachState? = null
        val foreachStates = mutableListOf<ForeachState?>()
        val rowsToBeIgnored = mutableListOf<Int>()
        val foreachTemplates = mutableMapOf<Int, String>()
        val decoratedRows = mutableMapOf<Int, List<String>>()
        rows.forEachIndexed {
            index, line ->
            // Trim comments that are not at the line start
            var row = line
            val lineCommentIndex = row.indexOf(tags.lineComment)
            if (!row.startsWith(tags.lineComment) && lineCommentIndex > 0) {
                row = row.substring(0, lineCommentIndex)
            }
            // Trim tab placeholder
            row = row.replace(tags.tabPlacegolder, "")
            rows[index] = row

            // Parse <include> tags
            val pInclude = Pattern.compile("${tags.includeOpen}(.+?)${tags.includeClose}")
            val mInclude = pInclude.matcher(line)
            while (mInclude.find()) {
                val include = mInclude.group(1)
                var element = decorate(include, data)
                if (element.endsWith("\n")) {
                    element = element.substring(0, element.lastIndex)
                }
                row = row.replace(mInclude.group(0), element)
                rows[index] = row
            }

            // Parse <if> tags
            val pIf = Pattern.compile("${tags.ifOpen}(.+?)${tags.ifClose}")
            val mIf = pIf.matcher(line)
            while (mIf.find()) {
                val ifCondition = mIf.group(1)
                val result = resolveIf(template, data, ifCondition, index)
                row = row.replace(mIf.group(0), "")
                if (row.isEmpty()) {
                    rowsToBeIgnored.add(index)
                }
                rows[index] = row
                if (ifState != null) {
                    throw IllegalStateException(Messages.IF_NOT_CLOSED(template, index))
                } else {
                    ifState = IfState(index, -1, result)
                }
            }

            // Parse <else> tags
            val pElse = Pattern.compile(tags.elseTag)
            val mElse = pElse.matcher(line)
            while (mElse.find()) {
                row = row.replace(mElse.group(0), "")
                if (row.isEmpty()) {
                    rowsToBeIgnored.add(index)
                }
                rows[index] = row
                if (ifState == null) {
                    throw IllegalStateException(Messages.IF_NOT_OPENED(template, index))
                } else {
                    if (elseState != null) {
                        throw IllegalStateException(Messages.ELSE_NOT_CLOSED(template, index))
                    } else {
                        elseState = ElseState(index, -1)
                    }
                }
            }

            // Parse <endif/> tag
            val pEndIf = Pattern.compile(tags.endIf)
            val mEndIf = pEndIf.matcher(line)
            while (mEndIf.find()) {
                row = row.replace(mEndIf.group(0), "")
                if (row.isEmpty()) {
                    rowsToBeIgnored.add(index)
                }
                rows[index] = row
                if (ifState != null) {
                    ifState?.to = index
                    ifStates.add(ifState)
                    ifState = null
                } else {
                    throw IllegalStateException(Messages.IF_NOT_OPENED(template, index))
                }
                if (elseState != null) {
                    elseState?.to = index
                    elseStates.add(elseState)
                    elseState = null
                }
            }

            // Parse <foreach>
            val pFor = Pattern.compile("${tags.foreachOpen}(.+?)${tags.foreachClose}")
            val mFor = pFor.matcher(line)
            while (mFor.find()) {
                val forCondition = mFor.group(1)
                row = row.replace(mFor.group(0), "")
                if (!row.isEmpty()) {
                    throw IllegalStateException(Messages.CONTENT_AFTER_FOR_OPENING(template, index))
                }
                rows[index] = row
                if (foreachState != null) {
                    throw IllegalStateException(Messages.FOR_NOT_CLOSED(template, index))
                } else {
                    foreachState = ForeachState(index, -1, forCondition)
                }
            }

            // Parse <endfor/>
            val pEndfor = Pattern.compile(tags.endFor)
            val mEndfor = pEndfor.matcher(line)
            while (mEndfor.find()) {
                row = row.replace(mEndfor.group(0), "")
                if (row.isEmpty()) {
                    rowsToBeIgnored.add(index)
                }
                rows[index] = row
                if (foreachState == null) {
                    throw IllegalStateException(Messages.FOR_NOT_OPENED(template, index))
                } else {
                    foreachState?.to = index
                    foreachStates.add(foreachState)
                    foreachState = null
                }
            }

            // Parse <dc> tags
            var foreachStateInProgress = false
            val currentState = foreachState
            currentState?.let {
                foreachStateInProgress = index > currentState.from
            }
            if (foreachStateInProgress) {
                foreachTemplates[index] = line
                rowsToBeIgnored.add(index)
            } else {
                val p = Pattern.compile("${tags.open}(.+?)${tags.close}")
                val m = p.matcher(line)
                val commands = mutableListOf<String>()
                while (m.find()) {
                    val result = m.group()
                    commands.add(result)
                }
                if (!commands.isEmpty()) {
                    decoratedRows[index] = commands
                }
            }
        }

        foreachStates.forEach {
            item ->
            logger.e("", ">>>> ${item?.value}")
        }

        rows.forEachIndexed {
            index, line ->

            val state = getForeachState(foreachStates, index)
            state?.let {
                if(state.from == index) {
                    logger.e("", "[ ${rowsToBeIgnored.contains(index)} ] >>> $line")
                }
            }

            var isLineValid = !line.startsWith(tags.lineComment)
            if (isLineValid && !satisfiesIf(ifStates, index)) {
                isLineValid = satisfiesElse(elseStates, index)
            }
            if (!rowsToBeIgnored.contains(index) && isLineValid) {
                var renderedLine = line
                if (decoratedRows.containsKey(index)) {
                    decoratedRows[index]?.forEach {
                        row ->
                        val decoration = row
                                .replace(tags.open, "")
                                .replace(tags.close, "")
                                .trim()
                        val result = resolve(template, data, decoration, index)
                        renderedLine = renderedLine.replace(row, result)
                    }
                }
                rendered.append("$renderedLine\n")
            }
        }
        return rendered.toString()
    }

    private fun resolve(template: String, templateData: Data, line: String, position: Int): String {
        val params = line.trim().split(memberSeparator.value)
        templateData.content.putAll(templateMainClass.data.content)
        val it = params.iterator()
        var data: TemplateData? = null
        if (it.hasNext()) {
            data = templateData.content[it.next()]
        }
        while (data != null && data !is Value && it.hasNext()) {
            when (data) {
                is Data -> {
                    val param = it.next()
                    data = data.content[param]
                }
                is Value -> {
                    return data.content
                }
                else -> throw IllegalStateException(Messages.UNKNOWN_TEMPLATE_DATA_TYPE)
            }
        }
        if (data != null && data is Value) {
            return data.content
        } else {
            if (data is Collection) {
                throw IllegalArgumentException(Messages.COLLECTION_NOT_ALLOWED(line, template, position))
            } else {
                throw IllegalArgumentException(Messages.COULD_NOT_RESOLVE(line, template, position))
            }
        }
    }

    private fun resolveIf(template: String, templateData: Data, line: String, position: Int): Boolean {
        val delegate = object : TautologyParserDelegate {
            override fun getExpressionValue(key: String): ExpressionValue? {
                val resolve: String?
                try {
                    resolve = resolve(template, templateData, key, position)
                    return object : ExpressionValue {
                        override fun getValue(): Boolean {
                            return !resolve.isEmpty()
                        }
                    }
                } catch (e: Exception) {
                    logger.w("", "$e")
                }
                return null
            }
        }
        val parser = TautologyParser(delegate)
        val expressions = parser.parse(line)
        return tautology.evaluate(expressions)
    }

    private fun satisfiesIf(ifStates: List<IfState?>, index: Int): Boolean {
        ifStates.forEach {
            ifState ->
            if (ifState != null) {
                if (index >= ifState.from && index <= ifState.to) {
                    return ifState.value
                }
            }
        }
        return true
    }

    private fun satisfiesElse(elseStates: List<ElseState?>, index: Int): Boolean {
        elseStates.forEach {
            ifState ->
            if (ifState != null) {
                if (index >= ifState.from && index <= ifState.to) {
                    return true
                }
            }
        }
        return false
    }

    private fun getForeachState(foreachStates: List<ForeachState?>, index: Int): ForeachState? {
        foreachStates.forEach {
            foreachState ->
            if (foreachState != null) {
                if (index >= foreachState.from && index <= foreachState.to) {
                    return foreachState
                }
            }
        }
        return null
    }

}
