package net.milosvasic.decorator.template

abstract class Tags {

    abstract val open: String
    abstract val close: String
    abstract val includeOpen: String
    abstract val includeClose: String
    abstract val ifOpen: String
    abstract val ifClose: String
    abstract val endIf: String
    abstract val elseTag: String
    abstract val foreachOpen: String
    abstract val foreachClose: String
    abstract val endFor: String
    abstract val lineComment: String
    abstract val trueTag: String
    abstract val falseTag: String

}