package net.milosvasic.decorator.content

object Messages {

    fun UNKNOWN_OPERATION(what: String, who: String, where: Int): String {
        return "Unknown operation ${describe(what, who, where)}"
    }

    fun NO_ARGUMENTS_PROVIDED_FOR(what: String, who: String, where: Int): String {
        return "No arguments provided to ${describe(what, who, where)}"
    }

    fun INVALID_ARGUMENTS_PASSED(what: String, who: String, where: Int): String {
        return "Invalid arguments passed to ${describe(what, who, where)}"
    }

    fun INVALID_INVOKE_ARGUMENTS(what: String, args: String): String {
        return "Could no invoke '$what' with arguments: $args"
    }

    private fun describe(what: String, who: String, where: Int): String {
        return "'$what' from '$who.decorator' at line: ${where + 1}"
    }

}