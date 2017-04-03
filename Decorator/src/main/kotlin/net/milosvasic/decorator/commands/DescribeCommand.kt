package net.milosvasic.decorator.commands

import net.milosvasic.decorator.BuildConfig
import net.milosvasic.decorator.Decorator
import net.milosvasic.decorator.evaluation.ContentResult
import kotlin.reflect.KClass

class DescribeCommand : TemplateCommand() {

    override val name: String = "describe"
    override val parameters = listOf<KClass<*>>()

    override fun invoke(parameters: List<String>): ContentResult {
        checkParameters(parameters)
        val content = StringBuilder()
                .append("<!-- Template system: ${Decorator::class.simpleName}, https://github.com/milos85vasic/Decorator -->\n")
                .append("<!-- Template system version: ${BuildConfig.VERSION.replace("_", " ")} -->")
        return ContentResult(content.toString())
    }

}

