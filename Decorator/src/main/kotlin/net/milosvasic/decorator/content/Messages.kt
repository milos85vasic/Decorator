package net.milosvasic.decorator.content

object Messages {

    fun UNKNOWN_OPERATION(what: String, who: String, where: Int): String {
        return "Unknown operation ${describe(what, who, where)}"
    }

    fun UNKNOWN_TEMPLATE_MEMBER(what: String, who: String, where: Int): String {
        return "Unknown template member ${describe(what, who, where)}"
    }

    fun NO_ARGUMENTS_PROVIDED_FOR(what: String, who: String, where: Int): String {
        return "No arguments provided to ${describe(what, who, where)}"
    }

    fun INVALID_ARGUMENTS_PASSED(what: String, who: String, where: Int): String {
        return "Invalid arguments passed to ${describe(what, who, where)}"
    }

    fun INVALID_INVOKE_ARGUMENTS(what: String, args: String): String {
        return "Could no invoke '$what'\n\t\targuments: $args"
    }

    fun UNKNOWN_TEMPLATE_DATA_TYPE(what: String, type: String?, who: String, where: Int): String {
        return "Unknown type passed '$type', ${describe(what, who, where)}"
    }

    private fun describe(what: String, who: String, where: Int): String {
        return "'$what' from '$who.decorator' at line: ${where + 1}"
    }

}