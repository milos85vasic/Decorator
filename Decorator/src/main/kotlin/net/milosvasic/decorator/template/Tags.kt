package net.milosvasic.decorator.template

abstract class Tags {

    abstract val open: String
    abstract val close: String
    abstract val includeOpen: String
    abstract val includeClose: String
    abstract val ifOpen: String
    abstract val ifClose: String
    abstract val endif: String
    abstract val elseTag: String


}