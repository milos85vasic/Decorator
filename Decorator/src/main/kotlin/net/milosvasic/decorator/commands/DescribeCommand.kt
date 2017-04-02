package net.milosvasic.decorator.commands

import net.milosvasic.decorator.BuildConfig
import net.milosvasic.decorator.Decorator
import kotlin.reflect.KClass

class DescribeCommand : TemplateCommand() {

    override val name: String = "describe"
    override val parameters = listOf<KClass<*>>()

    fun describe(): String {
        return "<!-- Template system: ${Decorator::class.simpleName} ${BuildConfig.VERSION.replace("_", " ")} -->"
    }

}

