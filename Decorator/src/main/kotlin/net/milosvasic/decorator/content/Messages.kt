package net.milosvasic.decorator.content

object Messages {

    val UNKNOWN_TEMPLATE_DATA_TYPE = "Unknown type passed."

    fun COULD_NOT_RESOLVE(line: String, template: String, position: Int): String {
        return "Could not resolve '$line' ${where(template, position)}"
    }

    fun COLLECTION_NOT_ALLOWED(line: String, template: String, position: Int): String {
        return "Collection not allowed here. '$line' ${where(template, position)}. Use 'foreach' iteration."
    }

    fun IF_NOT_OPENED(template: String, position: Int): String {
        return "'If' not opened ${where(template, position)}"
    }

    fun FOR_NOT_OPENED(template: String, position: Int): String {
        return "'For' not opened ${where(template, position)}"
    }

    fun IF_NOT_CLOSED(template: String, position: Int): String {
        return "'If' not closed ${where(template, position)}"
    }

    fun ELSE_NOT_CLOSED(template: String, position: Int): String {
        return "'Else' not closed ${where(template, position)}"
    }

    fun FOR_NOT_CLOSED(template: String, position: Int): String {
        return "'Foreach' not closed ${where(template, position)}"
    }

    fun CONTENT_AFTER_FOR_OPENING(template: String, position: Int): String {
        return "Decoration content not allowed on the same line where foreach is opened ${where(template, position)}"
    }

    fun ONLY_COLLECTION_ALLOWED(template: String, position: Int): String {
        return "Only collection data type allowed in foreach ${where(template, position)}"
    }

    fun COLLECTION_NOT_ALLOWED(template: String, position: Int): String {
        return "Collection data type not allowed here ${where(template, position)}"
    }

    private fun where(who: String, where: Int) = "from '$who.decorator' at line: ${where + 1}"

}