package net.milosvasic.decorator.commands

import kotlin.reflect.KClass

class PrintlnCommand : TemplateCommand() {

    override val name: String = "println"
    override val parameters = listOf<KClass<String>>()

    fun println(what: String): String {
        return what
    }

}

