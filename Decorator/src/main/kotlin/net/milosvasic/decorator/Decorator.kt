package net.milosvasic.decorator

class Decorator {

    val templateExtension = "decorator"

    fun decorate(template: String): String {
        val templateFile = javaClass.classLoader.getResource("$template.$templateExtension")
        val content = templateFile.readText()
        return content
    }

}
