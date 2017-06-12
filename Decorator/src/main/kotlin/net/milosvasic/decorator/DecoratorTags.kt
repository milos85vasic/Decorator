package net.milosvasic.decorator

import net.milosvasic.decorator.item.IterationItem
import net.milosvasic.decorator.template.Tags

class DecoratorTags : Tags(), IterationItem {

    val tabPlaceholder = "<t/>"
    val tagStart = "<"
    val tagEnd = ">"
    val tagClosedStart = "</"
    val tagClosedEnd = "/>"
    val ifWord = "if"
    val endifWord = "endif"
    val forWord = "foreach"
    val endforWord = "endfor"

    override val open = "${tagStart}dc$tagEnd"
    override val close = "${tagClosedStart}dc$tagEnd"
    override val includeOpen = "${tagStart}include$tagEnd"
    override val includeClose = "${tagClosedStart}include$tagEnd"
    override val ifOpen = "$tagStart$ifWord$tagEnd"
    override val ifClose = "$tagClosedStart$ifWord$tagEnd"
    override val elseTag = "${tagStart}else$tagEnd"
    override val endIf = "$tagStart$endifWord$tagClosedEnd"
    override val foreachOpen = "$tagStart$forWord$tagEnd"
    override val foreachClose = "$tagClosedStart$forWord$tagEnd"
    override val endFor = "$tagStart$endforWord$tagClosedEnd"
    override val lineComment = "//"
    override val newLine = "${tagStart}n$tagClosedEnd"
    override val itemTag: String = "${tagStart}item$tagEnd"
    override val indexTag: String = "${tagStart}index$tagEnd"
    override val multiLineCommentOpen = "/\\*"
    override val multiLineCommentClose = "\\*/"

}
