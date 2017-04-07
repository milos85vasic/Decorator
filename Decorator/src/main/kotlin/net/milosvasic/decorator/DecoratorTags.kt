package net.milosvasic.decorator

import net.milosvasic.decorator.template.Tags

class DecoratorTags : Tags(){

    override val open = "<dc>"
    override val close = "</dc>"
    override val includeOpen = "<include>"
    override val includeClose = "</include>"

}
