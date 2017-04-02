package net.milosvasic.decorator.commands

import net.milosvasic.decorator.content.Messages
import net.milosvasic.decorator.evaluation.EvaluationResult
import kotlin.reflect.KClass

abstract class TemplateCommand {

    abstract val name: String
    abstract val parameters: List<KClass<*>>

    abstract fun invoke(parameters: List<String> = listOf()): EvaluationResult

    protected fun checkParameters(parameters: List<String>) {
        if (parameters.size != this@TemplateCommand.parameters.size) {
            val printableArgs = StringBuilder()
            parameters.forEachIndexed {
                index, arg ->
                printableArgs.append(arg)
                if (index < parameters.lastIndex) {
                    printableArgs.append(", ")
                }
            }
            throw IllegalArgumentException(
                    Messages.INVALID_INVOKE_ARGUMENTS(name, printableArgs.toString())
            )
        }
    }

}