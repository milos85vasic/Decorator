package net.milosvasic.decorator

import net.milosvasic.decorator.content.Messages
import net.milosvasic.decorator.evaluation.ContentResult
import net.milosvasic.decorator.evaluation.EvaluationResult
import net.milosvasic.decorator.template.TemplateSystem
import net.milosvasic.logger.SimpleLogger
import java.util.regex.Pattern

class Decorator : TemplateSystem {

    private val logger = SimpleLogger()
    override val openingTag: String = "<dc>"
    override val closingTag: String = "</dc>"
    override val templateExtension = "decorator"
    override val templateMainClass = DecoratorTemplateClass()

    override fun decorate(template: String): String {
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
                    val evaluationResult = evaluate(template, decoration, index)
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

    fun evaluate(template: String, line: String, position: Int): EvaluationResult {
        logger.d("", line)
        val p = Pattern.compile("(\\w+)")
        val m = p.matcher(line)
        val params = mutableListOf<String>()
        val clazzName = this::class.simpleName?.toLowerCase()
        while (m.find()) {
            val param = m.group(0)
            params.add(param)
            logger.c("", "PARAM: [ $param ]")
            logger.c("", "- - - - - -")
        }
        if (!params.isEmpty()) {
            if (params[0] == clazzName) {
                if (params.size == 1) {
                    throw IllegalArgumentException(
                            Messages.NO_ARGUMENTS_PROVIDED_FOR(clazzName, template, position)
                    )
                } else {
                    val command = templateMainClass.commands[params[1]]
                    if (command != null) {
                        if (params.size > 2 + command.parameters.size) {
                            throw IllegalArgumentException(
                                    Messages.INVALID_ARGUMENTS_PASSED(
                                            "$clazzName.${params[1]}",
                                            template,
                                            position
                                    )
                            )
                        }
                        val commandParams = mutableListOf<String>()
                        if (params.size >= 2) {
                            commandParams.addAll(params.subList(2, params.lastIndex))
                        }
                        return command.invoke(commandParams)
                    } else {
                        throw IllegalArgumentException(
                                Messages.UNKNOWN_OPERATION(
                                        "$clazzName.${params[1]}",
                                        template,
                                        position
                                )
                        )
                    }
                }
            }
        } else {
            // TODO: Handle other than decorator methods.
        }
        return ContentResult("[ ... ]")
    }

}
