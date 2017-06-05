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


//        val rows = mutableListOf<String>()
//        rows.addAll(content.split("\n"))
//        var ifStatesOpened = 0
//        val ifStates = mutableListOf<IfState?>()
//        var elseStatesOpened = 0
//        val elseStates = mutableListOf<ElseState?>()
//        var foreachState: ForeachState? = null
//        val foreachStates = mutableListOf<ForeachState?>()
//        val rowsToBeIgnored = mutableListOf<Int>()
//        val foreachTemplates = mutableMapOf<Int, String>()
//        val decoratedRows = mutableMapOf<Int, List<String>>()

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
                                            .replace("${tags.open}${tags.indexTag}${tags.close}", index.toString())
                                            .replace(Regex("${tags.open}${tags.itemTag}${tags.close}"), tData.content)
                            )
                        }
                        is Data -> {
                            var row = item.replace("${tags.open}${tags.indexTag}${tags.close}", index.toString())
                            val pIf = Pattern.compile("${tags.ifOpen}${tags.itemTag}.(.+?)${tags.ifClose}")
                            val mIf = pIf.matcher(row)
                            while (mIf.find()) {
                                val partParams = mIf.group(1).split(memberSeparator.value)
                                partParams.forEach {
                                    part ->
                                    var partsData: TemplateData? = null
                                    val partIt = partParams.iterator()
                                    if (partIt.hasNext()) {
                                        val param = partIt.next()
                                        partsData = tData.content[param]
                                    }
                                    if (partIt.hasNext()) {
                                        val param = partIt.next()
                                        partsData = tData.content[param]
                                    }
                                    while (partsData != null && partsData !is Value && partIt.hasNext()) {
                                        when (partsData) {
                                            is Data -> {
                                                val param = partIt.next()
                                                partsData = partsData.content[param]
                                            }
                                            is Value -> {
                                                // Ignore
                                            }
                                            else -> throw IllegalStateException(Messages.COLLECTION_NOT_ALLOWED(template))
                                        }
                                    }
                                    if (partsData != null) {
                                        if (partsData is Value) {
                                            if (partsData.content.isEmpty()) {
                                                row = row.replaceFirst(mIf.group(0), "${tags.ifOpen}${tags.falseTag}${tags.ifClose}")
                                            } else {
                                                row = row.replaceFirst(mIf.group(0), "${tags.ifOpen}${tags.trueTag}${tags.ifClose}")
                                            }
                                        } else {
                                            throw IllegalStateException(Messages.COULD_NOT_RESOLVE(part, template))
                                        }
                                    } else {
                                        try {
                                            val resolved = resolve(template, templateData, part)
                                            if (resolved.isEmpty()) {
                                                row = row.replaceFirst(mIf.group(0), "${tags.ifOpen}${tags.falseTag}${tags.ifClose}")
                                            } else {
                                                row = row.replaceFirst(mIf.group(0), "${tags.ifOpen}${tags.trueTag}${tags.ifClose}")
                                            }
                                        } catch (e: Exception) {
                                            row = row.replaceFirst(mIf.group(0), "${tags.ifOpen}${tags.falseTag}${tags.ifClose}")
                                        }
                                    }
                                }
                            }

                            val p = Pattern.compile("${tags.open}(.+?)${tags.close}")
                            val m = p.matcher(row)
                            val parsedParts = mutableListOf<String>()
                            while (m.find()) {
                                val result = m.group(1).trim()
                                parsedParts.add(result)
                            }
                            parsedParts.forEach {
                                part ->
                                val partParams = part
                                        .replace("${tags.itemTag}${memberSeparator.value}", "")
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
                    when (key) {
                        tags.trueTag -> {
                            resolve = tags.trueTag
                        }
                        tags.falseTag -> {
                            resolve = ""
                        }
                        else -> {
                            resolve = resolve(template, templateData, key)
                        }
                    }
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
        val expressions = parser.parse(line)
        return tautology.evaluate(expressions)
    }

    private fun satisfiesIf(ifStates: List<IfState?>, index: Int, template: String): Boolean {
        ifStates.forEach {
            state ->
            if (state != null) {
                if (state.to == -1) {
                    throw IllegalStateException(Messages.IF_NOT_CLOSED(template))
                }
                if (index >= state.from && index <= state.to) {
                    return state.value
                }
            }
        }
        return true
    }

    private fun satisfiesElse(elseStates: List<ElseState?>, index: Int, template: String): Boolean {
        elseStates.forEach {
            state ->
            if (state != null) {
                if (state.to == -1) {
                    throw IllegalStateException(Messages.ELSE_NOT_CLOSED(template))
                }
                if (index >= state.from && index <= state.to) {
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
