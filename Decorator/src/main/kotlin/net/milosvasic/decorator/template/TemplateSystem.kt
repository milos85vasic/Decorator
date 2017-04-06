package net.milosvasic.decorator.template

import net.milosvasic.decorator.data.Data


interface TemplateSystem {

    val openingTag: String
    val closingTag: String
    val templateExtension: String
    val templateMainClass: TemplateClass

    fun decorate(template: String, data: Data = Data()): String

}