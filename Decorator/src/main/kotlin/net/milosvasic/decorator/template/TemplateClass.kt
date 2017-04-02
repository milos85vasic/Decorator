package net.milosvasic.decorator.template

import net.milosvasic.decorator.commands.TemplateCommand

interface TemplateClass {

    val commands : Map<String, TemplateCommand>

}