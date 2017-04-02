package net.milosvasic.decorator.template


interface TemplateSystem {

    val openingTag : String
    val closingTag : String
    val templateExtension : String
    val templateMainClass : TemplateClass

    fun decorate(template: String): String

}