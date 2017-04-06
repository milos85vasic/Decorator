package net.milosvasic.decorator


import net.milosvasic.decorator.data.Data
import net.milosvasic.decorator.data.Value
import net.milosvasic.decorator.template.TemplateClass

class DecoratorTemplateClass : TemplateClass() {

    init {
        val decorator = Data()
        val description = getDescription()
        data.content["decorator"] = decorator
        decorator.content["describe"] = Value(description)
    }

    private fun getDescription(): String {
        return StringBuilder()
                .append("<!-- Template system: ${Decorator::class.simpleName}, https://github.com/milos85vasic/Decorator -->\n")
                .append("<!-- Template system version: ${BuildConfig.VERSION.replace("_", " ")} -->")
                .toString()
    }

}

