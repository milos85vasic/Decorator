package net.milosvasic.decorator


import net.milosvasic.decorator.template.TemplateClass

class DecoratorTemplateClass : TemplateClass {

    override fun describe(): String {
        return "<!-- Template system: ${Decorator::class.simpleName} ${BuildConfig.VERSION.replace("_", " ")} -->"
    }

    override fun println(what: String): String {
        return what
    }

}

