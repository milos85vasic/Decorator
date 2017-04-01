package net.milosvasic.decorator.content

object Messages {

    fun UNKNOWN_OPERATION(what: String, where: Int) = "Unknown operation '$what' at line: ${where + 1}"
    fun INVALID_ARGUMENTS_PASSED(what: String, where: Int) = "Invalid arguments passed '$what' at line: ${where + 1}"
    fun NO_ARGUMENTS_PROVIDED_FOR(what: String, where: Int) = "No arguments provided for '$what' at line: ${where + 1}"

}