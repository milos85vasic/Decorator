package net.milosvasic.decorator


import net.milosvasic.decorator.commands.DescribeCommand
import net.milosvasic.decorator.commands.PrintlnCommand
import net.milosvasic.decorator.template.TemplateClass

class DecoratorTemplateClass : TemplateClass {

    override val commands = mapOf(
            Pair("describe", DescribeCommand()),
            Pair("println", PrintlnCommand()),
            Pair("echo", PrintlnCommand()) // Synonym for println
    )

}

