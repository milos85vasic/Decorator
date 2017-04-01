package net.milosvasic.decorator

import net.milosvasic.decorator.template.TemplateClass

interface TemplatingSystem {

    val openingTag : String
    val closingTag : String
    val templateExtension : String
    val templateMainClass : TemplateClass

    fun decorate(template: String): String

}