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
            decoratedRows[index] = commands
            commands.forEach(::println)
        }


        /*
            .replace(openingTag, "")
            .replace(closingTag, "")
            .trim()
         */

        return rendered.toString()
    }

}
