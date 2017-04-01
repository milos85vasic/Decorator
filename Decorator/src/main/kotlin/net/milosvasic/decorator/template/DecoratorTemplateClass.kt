package net.milosvasic.decorator.template

import net.milosvasic.decorator.BuildConfig
import net.milosvasic.decorator.Decorator

class DecoratorTemplateClass : TemplateClass {

    override fun describe(): String {
        return "<!-- Template system: ${Decorator::class.simpleName} ${BuildConfig.VERSION.replace("_", " ")} -->"
    }

    override fun println(what: String): String {
        return what
    }

}

