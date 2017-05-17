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
        decorator.content["xmlns"] = Value("xmlns=\"http://www.w3.org/1999/xhtml\"")
        decorator.content["doctype"] = Value("\"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"")
    }

    private fun getDescription(): String {
        return StringBuilder()
                .append("<!-- Template system: ${Decorator::class.simpleName}, https://github.com/milos85vasic/Decorator -->\n")
                .append("<!-- Template system version: ${BuildConfig.VERSION.replace("_", " ")} -->")
                .toString()
    }

}

