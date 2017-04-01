package net.milosvasic.decorator

import java.util.regex.Pattern

class DecoratorImpl : Decorator {

    override val openingTag: String = "<dc>"
    override val closingTag: String = "</dc>"
    override val templateExtension = "decorator"

    override fun decorate(template: String): String {
        val templateFile = javaClass.classLoader.getResource("$template.$templateExtension")
        var rendered = StringBuilder()
        val content = templateFile.readText()
        val rows = content.split("\n")
        val decoratedRows = mutableMapOf<Int, String>()
        rows.forEachIndexed {
            index, line ->
            val p = Pattern.compile("$openingTag(.+?)$closingTag")
            val m = p.matcher(line)
            while (m.find()) {
                val result = m
                        .group()
                        .replace(openingTag, "")
                        .replace(closingTag, "")
                        .trim()
                decoratedRows[index] = result
                println(result)
            }
        }
        return rendered.toString()
    }

}
