package net.milosvasic.decorator

interface Decorator {

    val openingTag : String
    val closingTag : String
    val templateExtension : String

    fun decorate(template: String): String

}