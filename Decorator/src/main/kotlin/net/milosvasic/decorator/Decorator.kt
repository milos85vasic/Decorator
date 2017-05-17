package net.milosvasic.decorator

import net.milosvasic.decorator.content.Messages
import net.milosvasic.decorator.data.Data
import net.milosvasic.decorator.data.TemplateData
import net.milosvasic.decorator.data.Value
import net.milosvasic.decorator.data.state.ElseState
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
    override val templateExtension = "decorator"
    override val memberSeparator = Separator.MEMBER()
    override val templateMainClass = DecoratorTemplateClass()

    private val tautology = Tautology()
    private val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))

    override fun decorate(template: String, data: Data): String {
        val rendered = StringBuilder()
        val templateFile = javaClass.classLoader.getResource("$template.$templateExtension")
        val content = templateFile.readText()
        val rows = mutableListOf<String>()
        rows.addAll(content.split("\n"))
        var ifState: IfState? = null
        val ifStates = mutableListOf<IfState?>()
        var elseState: ElseState? = null
        val elseStates = mutableListOf<ElseState?>()
        val rowsToBeIgnored = mutableListOf<Int>()
        val decoratedRows = mutableMapOf<Int, List<String>>()
        rows.forEachIndexed {
            index, line ->
            // Parse <include> tags
            val pInclude = Pattern.compile("${tags.includeOpen}(.+?)${tags.includeClose}")
            val mInclude = pInclude.matcher(line)
            var row = line
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

            // Parse for
            val pFor = Pattern.compile("${tags.foreachOpen}(.+?)${tags.foreachClose}")
            val mFor = pFor.matcher(line)
            while(mFor.find()){
                val forCondition = mFor.group(1)
                row = row.replace(mFor.group(0), "")
                if (row.isEmpty()) {
                    rowsToBeIgnored.add(index)
                }
                logger.i("", "FOR: $forCondition")
            }


            // Parse <dc> tags
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

        rows.forEachIndexed {
            index, line ->
            var isLineValid = !line.startsWith("//")
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
                is Collection<*> -> {
                    logger.e("", "Collection ...")
                    throw IllegalStateException(Messages.UNKNOWN_TEMPLATE_DATA_TYPE)
                }
                else -> throw IllegalStateException(Messages.UNKNOWN_TEMPLATE_DATA_TYPE)
            }
        }
        if (data != null && data is Value) {
            return data.content
        } else {
            throw IllegalArgumentException(Messages.COULD_NOT_RESOLVE(line, template, position))
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

}
