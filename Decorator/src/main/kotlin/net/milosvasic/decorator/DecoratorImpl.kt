package net.milosvasic.decorator

import net.milosvasic.decorator.evaluation.ContentResult
import net.milosvasic.decorator.evaluation.EvaluationResult
import java.util.regex.Pattern

class DecoratorImpl : Decorator {

    override val openingTag: String = "<dc>"
    override val closingTag: String = "</dc>"
    override val templateExtension = "decorator"

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
            commands.forEach(::println)
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
                    val evaluationResult = evaluate(decoration)
                    when (evaluationResult) {
                        is ContentResult -> {
                            renderedLine = line.replace(row, evaluationResult.content)
                        }
                    }
                }

            }
            rendered.append("$renderedLine\n")
        }
        return rendered.toString()
    }

    fun evaluate(line: String): EvaluationResult {
        println("EVAL: $line")
        return ContentResult("[ ... ]")
    }

}
