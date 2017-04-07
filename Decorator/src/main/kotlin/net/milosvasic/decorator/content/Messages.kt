package net.milosvasic.decorator.content

object Messages {

    val UNKNOWN_TEMPLATE_DATA_TYPE = "Unknown type passed."

    fun COULD_NOT_RESOLVE(line: String, template: String, position: Int): String {
        return "Could not resolve '$line' ${where(template, position)}"
    }

    fun INVALID_INVOKE_ARGUMENTS(what: String, args: String): String {
        return "Could not invoke '$what'\n\t\targuments: $args"
    }

    private fun describe(what: String, who: String, where: Int): String {
        return "'$what' ${where(who, where)}"
    }

    private fun where(who: String, where: Int) = "from '$who.decorator' at line: ${where + 1}"

}