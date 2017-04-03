package net.milosvasic.decorator.commands

import net.milosvasic.decorator.evaluation.ContentResult

class PrintlnCommand : TemplateCommand() {

    override val name: String = "println"
    override val parameters = listOf(String::class)

    override fun invoke(parameters: List<String>): ContentResult {
        checkParameters(parameters)
        var toPrint = parameters[0]
        if (toPrint.startsWith("\"")) {
            toPrint = toPrint.substring(1, toPrint.lastIndex)
        }
        if (toPrint.endsWith("\"")) {
            toPrint = toPrint.substring(toPrint.lastIndex - 1, toPrint.lastIndex)
        }
        val regex = Regex("(\\$\\w+)")
        toPrint = toPrint.replace(regex, "- - - -") // TODO: Match property
        return ContentResult(toPrint)
    }

}

