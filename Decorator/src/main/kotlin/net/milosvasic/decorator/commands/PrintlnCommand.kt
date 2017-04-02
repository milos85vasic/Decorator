package net.milosvasic.decorator.commands

import net.milosvasic.decorator.evaluation.ContentResult
import kotlin.reflect.KClass

class PrintlnCommand : TemplateCommand() {

    override val name: String = "println"
    override val parameters = listOf<KClass<String>>()

    override fun invoke(parameters: List<String>): ContentResult {
        checkParameters(parameters)
        return ContentResult(parameters[0])
    }

}

