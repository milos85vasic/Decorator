package net.milosvasic.decorator.content

object Messages {

    val UNKNOWN_TEMPLATE_DATA_TYPE = "Unknown type passed."

    fun COULD_NOT_RESOLVE(line: String, template: String, position: Int): String {
        return "Could not resolve '$line' ${where(template, position)}"
    }

    fun IF_NOT_OPENED(template: String, position: Int): String {
        return "'If' statement not opened ${where(template, position)}"
    }

    fun IF_NOT_CLOSED(template: String, position: Int): String {
        return "'If' statement not closed ${where(template, position)}"
    }

    private fun where(who: String, where: Int) = "from '$who.decorator' at line: ${where + 1}"

}