package net.milosvasic.decorator

interface Decorator {

    val templateExtension : String

    fun decorate(template: String): String

}