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
            var row = line

            // Trim comments that are not at the line start
            val lineCommentIndex = row.indexOf(tags.lineComment)
            if (!row.startsWith(tags.lineComment) && lineCommentIndex > 0) {
                row = row.substring(0, lineCommentIndex)
            }
            rows[index] = row

            // Trim comments that are at line start:
            if (row.startsWith(tags.lineComment)) {
                row = ""
                rows[index] = row
                rowsToBeIgnored.add(index)
            }

            // Trim tab placeholder
            row = row.replace(tags.tabPlaceholder, "")
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
        }

        rows.forEachIndexed {
            index, line ->
            var row = line
            // Parse <foreach>
            val pFor = Pattern.compile("${tags.foreachOpen}(.+?)${tags.foreachClose}")
            val mFor = pFor.matcher(line)
            while (mFor.find()) {
                val forCondition = mFor.group(1)
                row = row.replace(mFor.group(0), "")
                rows[index] = row
                if (!row.isEmpty()) {
                    throw IllegalStateException(Messages.CONTENT_AFTER_FOR_OPENING(template))
                }
                if (foreachState != null) {
                    throw IllegalStateException(Messages.FOR_NOT_CLOSED(template))
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
                    throw IllegalStateException(Messages.FOR_NOT_OPENED(template))
                } else {
                    foreachState?.to = index
                    foreachStates.add(foreachState)
                    foreachState = null
                }
            }

            var foreachStateInProgress = false
            val currentState = foreachState
            currentState?.let {
                foreachStateInProgress = index > currentState.from
            }
            if (foreachStateInProgress) {
                foreachTemplates[index] = line
                rowsToBeIgnored.add(index)
            }
        }

        rows.forEachIndexed {
            index, line ->
            var row = line
            val state = getForeachState(foreachStates, index)
            state?.let {
                if (state.from == index) {
                    val templateRows = mutableListOf<String>()
                    for (x in state.from..state.to) {
                        foreachTemplates[x]?.let {
                            item ->
                            templateRows.add(item)
                        }
                    }
                    row = resolveForeach(template, templateRows, data, state.value)
                }
            }
            rows[index] = row
        }

        rows.forEachIndexed {
            index, line ->
            var row = line

            // Parse <if> tags
            val pIf = Pattern.compile("${tags.ifOpen}(.+?)${tags.ifClose}")
            val mIf = pIf.matcher(line)
            while (mIf.find()) {
                val ifCondition = mIf.group(1)
                val result = resolveIf(template, data, ifCondition)
                row = row.replace(mIf.group(0), "")
                if (row.isEmpty()) {
                    rowsToBeIgnored.add(index)
                }
                rows[index] = row
                if (ifState != null) {
                    throw IllegalStateException(Messages.IF_NOT_CLOSED(template))
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
                    throw IllegalStateException(Messages.IF_NOT_OPENED(template))
                } else {
                    if (elseState != null) {
                        throw IllegalStateException(Messages.ELSE_NOT_CLOSED(template))
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
                    throw IllegalStateException(Messages.IF_NOT_OPENED(template))
                }
                if (elseState != null) {
                    elseState?.to = index
                    elseStates.add(elseState)
                    elseState = null
                }
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
            val isLineValid : Boolean
            val satisfiesElse = satisfiesElse(elseStates, index)
            if (!satisfiesIf(ifStates, index)) {
                isLineValid = satisfiesElse
            } else {
                isLineValid = !satisfiesElse
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
                        val result = resolve(template, data, decoration)
                        renderedLine = renderedLine.replace(row, result)
                    }
                }
                rendered.append("$renderedLine\n")
            }
        }
        return rendered.toString()
    }

    private fun resolveForeach(
            template: String,
            templateRows: List<String>,
            templateData: Data,
            templateDataKey: String
    ): String {
        val params = templateDataKey.trim().split(memberSeparator.value)
        var data: TemplateData? = null
        val it = params.iterator()
        if (it.hasNext()) {
            data = templateData.content[it.next()]
        }
        loop@ while (data != null && data !is Collection && it.hasNext()) {
            when (data) {
                is Data -> {
                    val param = it.next()
                    data = data.content[param]
                }
                is Collection -> {
                    break@loop
                }
                else -> throw IllegalStateException(Messages.ONLY_COLLECTION_ALLOWED(template))
            }
        }

        if (data is Collection) {
            val builder = StringBuilder()
            val items = data.items
            items.forEachIndexed {
                index, tData ->
                templateRows.forEach {
                    item ->
                    when (tData) {
                        is Value -> {
                            builder.append(
                                    item
                                            .replace("${tags.open}index${tags.close}", index.toString())
                                            .replace(Regex("${tags.open}item${tags.close}"), tData.content)
                            )
                        }
                        is Data -> {
                            var row = item.replace("${tags.open}index${tags.close}", index.toString())
                            val p = Pattern.compile("${tags.open}(.+?)${tags.close}")
                            val m = p.matcher(row)
                            val parsedParts = mutableListOf<String>()
                            while (m.find()) {
                                val result = m.group(1)
                                parsedParts.add(result)
                            }
                            parsedParts.forEach {
                                part ->
                                val partParams = part
                                        .replace("item${memberSeparator.value}", "")
                                        .split(memberSeparator.value)
                                var partData: TemplateData? = null
                                val partIt = partParams.iterator()
                                if (partIt.hasNext()) {
                                    val param = partIt.next()
                                    partData = tData.content[param]
                                }
                                while (partData != null && partData !is Value && partIt.hasNext()) {
                                    when (partData) {
                                        is Data -> {
                                            val param = partIt.next()
                                            partData = partData.content[param]
                                        }
                                        is Value -> {
                                            // Ignore
                                        }
                                        else -> throw IllegalStateException(Messages.COLLECTION_NOT_ALLOWED(template))
                                    }
                                }
                                if (partData != null) {
                                    if (partData is Value) {
                                        row = row.replace("${tags.open}$part${tags.close}", partData.content)
                                    } else {
                                        throw IllegalStateException(Messages.COULD_NOT_RESOLVE(part, template))
                                    }
                                } else {
                                    try {
                                        row = row.replace(
                                                "${tags.open}$part${tags.close}", resolve(template, templateData, part)
                                        )
                                    } catch (e: Exception) {
                                        row = row.replace("${tags.open}$part${tags.close}", "")
                                    }
                                }
                            }
                            builder.append(row)
                        }
                        else -> {
                            throw IllegalStateException(Messages.COULD_NOT_RESOLVE(templateDataKey, template))
                        }
                    }
                    if (templateRows.indexOf(item) < templateRows.lastIndex) {
                        builder.append("\n")
                    }
                }
                if (tData != items.last()) {
                    builder.append("\n")
                }
            }
            return builder.toString()
        } else {
            if (data != null) {
                throw IllegalStateException(Messages.ONLY_COLLECTION_ALLOWED(template))
            } else {
                throw IllegalStateException(Messages.COULD_NOT_RESOLVE(templateDataKey, template))
            }
        }
    }

    private fun resolve(template: String, templateData: Data, line: String): String {
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
                throw IllegalArgumentException(Messages.COLLECTION_NOT_ALLOWED(line, template))
            } else {
                throw IllegalArgumentException(Messages.COULD_NOT_RESOLVE(line, template))
            }
        }
    }

    private fun resolveIf(template: String, templateData: Data, line: String): Boolean {
        val delegate = object : TautologyParserDelegate {
            override fun getExpressionValue(key: String): ExpressionValue? {
                val resolve: String?
                try {
                    resolve = resolve(template, templateData, key)
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
