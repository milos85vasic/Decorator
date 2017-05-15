package net.milosvasic.decorator.template

import net.milosvasic.decorator.data.Data
import net.milosvasic.decorator.separator.Separator


interface TemplateSystem {

    val tags: Tags
    val templateExtension: String
    val templateMainClass: TemplateClass
    val memberSeparator: Separator.MemberSeparator

    fun decorate(template: String, data: Data = Data()): String

}