package net.milosvasic.decorator

import net.milosvasic.decorator.content.Messages
import net.milosvasic.decorator.data.Data
import net.milosvasic.decorator.data.TemplateData
import net.milosvasic.decorator.data.Value
import net.milosvasic.decorator.evaluation.ContentResult
import net.milosvasic.decorator.evaluation.EvaluationResult
import net.milosvasic.decorator.template.TemplateSystem
import net.milosvasic.decorator.utils.Text
import net.milosvasic.logger.SimpleLogger
import java.lang.IllegalStateException
import java.util.regex.Pattern

class Decorator : TemplateSystem {

    private val logger = SimpleLogger()
    override val openingTag: String = "<dc>"
    override val closingTag: String = "</dc>"
    override val templateExtension = "decorator"
    override val templateMainClass = DecoratorTemplateClass()

    override fun decorate(template: String, data: Data): String {
        val rendered = StringBuilder()
        val templateFile = javaClass.classLoader.getResource("$template.$templateExtension")
        val content = templateFile.readText()
        val rows = content.split("\n")
        val decoratedRows = mutableMapOf<Int, List<String>>()
        rows.forEachIndexed {
            index, line ->
            val p = Pattern.compile("$openingTag(.+?)$closingTag")
            val m = p.matcher(line)
            val commands = mutableListOf<String>()
            while (m.find()) {
                val result = m.group()
                commands.add(result)
            }
            if (!commands.isEmpty()) {
                decoratedRows[index] = commands
            }

            // TODO: To be removed.
            commands.forEach {
                item ->
                logger.v("", item)
            }
        }
        rows.forEachIndexed {
            index, line ->
            var renderedLine = line
            if (decoratedRows.containsKey(index)) {
                decoratedRows[index]?.forEach {
                    row ->
                    val decoration = row
                            .replace(openingTag, "")
                            .replace(closingTag, "")
                            .trim()
                    val evaluationResult = evaluate(template, data, decoration, index)
                    when (evaluationResult) {
                        is ContentResult -> {
                            renderedLine = renderedLine.replace(row, evaluationResult.content)
                        }
                    }
                }

            }
            rendered.append("$renderedLine\n")
        }
        return rendered.toString()
    }

    fun evaluate(template: String, templateData: Data, line: String, position: Int): EvaluationResult {
        logger.d("", line) // TODO: Remove this.

        val params = line.trim().split(".")

        // TODO: Remove this logs.
        params.forEach {
            param ->
            logger.c("", "PARAM: [ $param ]")
        }
        logger.c("", "- - - - - -")

        templateData.content.putAll(templateMainClass.data.content)
        val it = params.iterator()
        var data: TemplateData? = null
        if (it.hasNext()) {
            data = templateData.content[it.next()]
        }
        while (data !is Value && it.hasNext()) {
            when(data){
                is Data -> {
                    val param = it.next()
                    data = data.content[param]
                }
                is Value -> {
                    return ContentResult(data.content)
                }
                else -> throw IllegalStateException(Messages.UNKNOWN_TEMPLATE_DATA_TYPE)
            }

        }
        if (data != null && data is Value) {
            return ContentResult(data.content)
        } else {
            throw IllegalArgumentException("errrrror")
        }


//        if (!params.isEmpty()) {
//            if (params[0] == clazzName) {
//                if (params.size == 1) {
//                    throw IllegalArgumentException(
//                            Messages.NO_ARGUMENTS_PROVIDED_FOR(clazzName, template, position)
//                    )
//                } else {
//                    val command = templateMainClass.commands[params[1]]
//                    if (command != null) {
//                        val commandParams = mutableListOf<String>()
//                        if (params.size > 2) {
//                            commandParams.addAll(params.subList(2, params.size))
//                        }
//                        try {
//                            return command.invoke(commandParams)
//                        } catch (e: IllegalArgumentException) {
//                            throw IllegalArgumentException(
//                                    "${e.message}\n\t\ttemplate: $template.decorator\n\t\tline: $position"
//                            )
//                        }
//                    } else {
//                        throw IllegalArgumentException(
//                                Messages.UNKNOWN_OPERATION(
//                                        "$clazzName.${params[1]}",
//                                        template,
//                                        position
//                                )
//                        )
//                    }
//                }
//            }
//        } else {
//            // TODO: Handle other than decorator methods.
//        }
        return ContentResult("[ ... ]") // TODO: Update this to different value.
    }

}
