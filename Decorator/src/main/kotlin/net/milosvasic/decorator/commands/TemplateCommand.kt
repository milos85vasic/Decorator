package net.milosvasic.decorator.commands

abstract class TemplateCommand {

    abstract val name: String
    abstract val parameters: Map<String, Int>

}