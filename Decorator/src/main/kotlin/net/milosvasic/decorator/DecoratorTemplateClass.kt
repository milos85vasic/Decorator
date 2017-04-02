package net.milosvasic.decorator


import net.milosvasic.decorator.commands.DescribeCommand
import net.milosvasic.decorator.commands.PrintlnCommand
import net.milosvasic.decorator.template.TemplateClass

class DecoratorTemplateClass : TemplateClass {

    val describe = DescribeCommand()
    val println = PrintlnCommand()

}

