package net.milosvasic.decorator.template

import net.milosvasic.decorator.data.Data


interface TemplateSystem {

    val tags: Tags
    val templateExtension: String
    val templateMainClass: TemplateClass

    fun decorate(template: String, data: Data = Data()): String

}