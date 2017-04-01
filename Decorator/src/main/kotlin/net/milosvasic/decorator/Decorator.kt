package net.milosvasic.decorator

import net.milosvasic.decorator.content.Messages
import net.milosvasic.decorator.evaluation.ContentResult
import net.milosvasic.decorator.evaluation.EvaluationResult
import net.milosvasic.decorator.template.DecoratorTemplateClass
import net.milosvasic.logger.SimpleLogger
import java.util.regex.Pattern

class Decorator : TemplateSystem {

    private val logger = SimpleLogger()
    override val openingTag: String = "<dc>"
    override val closingTag: String = "</dc>"
    override val templateExtension = "decorator"
    override val templateMainClass = DecoratorTemplateClass()

    override fun decorate(template: String): String {
        var rendered = StringBuilder()
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
                    val evaluationResult = evaluate(decoration, index)
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

    fun evaluate(line: String, position: Int): EvaluationResult {
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
            when (params[0]) {
                clazzName -> {
                    if (params.size == 1) {
                        throw IllegalArgumentException(
                                Messages.NO_ARGUMENTS_PROVIDED_FOR(clazzName, position)
                        )
                    } else {
                        when (params[1]) {
                            "describe" -> {
                                if (params.size > 2) {
                                    throw IllegalArgumentException(
                                            Messages.INVALID_ARGUMENTS_PASSED(
                                                    "$clazzName.${params[1]}", position
                                            )
                                    )
                                }
                                return ContentResult(templateMainClass.describe())
                            }
                            else -> throw IllegalArgumentException(
                                    Messages.UNKNOWN_OPERATION(
                                            "$clazzName.${params[1]}", position
                                    )
                            )
                        }
                    }
                }
            }
        }
        return ContentResult("[ ... ]")
    }

}
