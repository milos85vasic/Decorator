package net.milosvasic.decorator.commands

import kotlin.reflect.KClass

abstract class TemplateCommand {

    abstract val name: String
    abstract val parameters: List<KClass<*>>

}