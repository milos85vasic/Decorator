package net.milosvasic.decorator

import net.milosvasic.decorator.template.Tags

class DecoratorTags : Tags(){

    override val open = "<dc>"
    override val close = "</dc>"
    override val includeOpen = "<include>"
    override val includeClose = "</include>"
    override val ifOpen = "<if>"
    override val ifClose = "</if>"
    override val elseTag = "<else>"
    override val endIf = "<endif/>"
    override val foreachOpen = "<foreach>"
    override val foreachClose = "</foreach>"
    override val endForeach = "<endfor/>"

}
