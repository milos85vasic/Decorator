package net.milosvasic.decorator.template

import net.milosvasic.decorator.data.Data
import net.milosvasic.decorator.separator.Separator


abstract class Template(val template: String, val data: Data = Data()) {

    abstract val tags: Tags
    abstract val templateExtension: String
    abstract val templateMainClass: TemplateClass
    abstract val memberSeparator: Separator.MemberSeparator
    abstract val arrayOpenSeparator: Separator.MemberSeparator
    abstract val arrayCloseSeparator: Separator.MemberSeparator

    abstract fun getContent(): String

}