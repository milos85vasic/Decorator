package net.milosvasic.decorator

import net.milosvasic.decorator.content.Messages
import net.milosvasic.decorator.data.Data
import net.milosvasic.decorator.data.TemplateData
import net.milosvasic.decorator.data.Value
import net.milosvasic.decorator.template.TemplateSystem
import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration
import java.lang.IllegalStateException
import java.util.regex.Pattern

class Decorator : TemplateSystem {

    override val tags = DecoratorTags()
    override val templateExtension = "decorator"
    override val templateMainClass = DecoratorTemplateClass()
    private val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))

    override fun decorate(template: String, data: Data): String {
        val rendered = StringBuilder()
        val templateFile = javaClass.classLoader.getResource("$template.$templateExtension")
        val content = templateFile.readText()
        val rows = mutableListOf<String>()
        rows.addAll(content.split("\n"))
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
                logger.d("", "IF: [ $ifCondition ][ $result ]")
                // TODO: Use map with pairs - index -> if start-end.
            }

            // Parse <endif/> tag
            // TODO: Endif.

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
        return rendered.toString()
    }

    fun resolve(template: String, templateData: Data, line: String, position: Int): String {
        val params = line.trim().split(".")
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
            throw IllegalArgumentException(Messages.COULD_NOT_RESOLVE(line, template, position))
        }
    }

    fun resolveIf(template: String, templateData: Data, line: String, position: Int): Boolean {
        val params = line.trim().split(".")
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
                    return !data.content.isEmpty()
                }
                else -> throw IllegalStateException(Messages.UNKNOWN_TEMPLATE_DATA_TYPE)
            }
        }
        if (data != null && data is Value) {
            return !data.content.isEmpty()
        } else {
            throw IllegalArgumentException(Messages.COULD_NOT_RESOLVE(line, template, position))
        }
    }

}
