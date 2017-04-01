package net.milosvasic.decorator

class DecoratorImpl : Decorator {

    override val templateExtension = "decorator"

    override fun decorate(template: String): String {
        val templateFile = javaClass.classLoader.getResource("$template.$templateExtension")
        val content = templateFile.readText()
        return content
    }

}
