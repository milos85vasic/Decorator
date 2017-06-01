package net.milosvasic.decorator

import net.milosvasic.decorator.item.IterationItem
import net.milosvasic.decorator.template.Tags

class DecoratorTags : Tags(), IterationItem {

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
    override val endFor = "<endfor/>"
    override val lineComment = "//"
    override val itemTag: String = "<item>"
    override val indexTag: String = "<index>"
    override val trueTag: String = "<true>"
    override val falseTag: String = "<false>"

    val tabPlaceholder = "<t/>"

}
