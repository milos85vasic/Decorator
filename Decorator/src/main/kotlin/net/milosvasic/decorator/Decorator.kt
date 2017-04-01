package net.milosvasic.decorator

import net.milosvasic.decorator.evaluation.ContentResult
import net.milosvasic.decorator.evaluation.EvaluationResult
import net.milosvasic.decorator.template.DecoratorTemplateClass
import net.milosvasic.logger.SimpleLogger
import java.util.regex.Pattern

class Decorator : TemplatingSystem {

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
                    val evaluationResult = evaluate(decoration)
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

    fun evaluate(line: String): EvaluationResult {
        logger.d("", line)
        val clazzName = this::class.simpleName?.toLowerCase()
        val p = Pattern.compile("$clazzName\\.(\\w+)")
        val m = p.matcher(line)
        while (m.find()) {
            val operation = m.group(1)
            logger.c("", "[ $operation ]")
        }

        return ContentResult("[ ... ]")
    }

}
