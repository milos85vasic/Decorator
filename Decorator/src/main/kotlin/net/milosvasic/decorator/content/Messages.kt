package net.milosvasic.decorator.content

object Messages {

    val UNKNOWN_TEMPLATE_DATA_TYPE = "Unknown type passed."

    fun COULD_NOT_RESOLVE(line: String, template: String): String {
        return "Could not resolve '$line' ${where(template)}"
    }

    fun COLLECTION_NOT_ALLOWED(line: String, template: String): String {
        return "Collection not allowed here. '$line' ${where(template)}. Use 'foreach' iteration."
    }

    fun IF_NOT_OPENED(template: String): String {
        return "'If' not opened ${where(template)}"
    }

    fun FOR_NOT_OPENED(template: String): String {
        return "'For' not opened ${where(template)}"
    }

    fun IF_NOT_CLOSED(template: String): String {
        return "'If' not closed ${where(template)}"
    }

    fun ELSE_NOT_CLOSED(template: String): String {
        return "'Else' not closed ${where(template)}"
    }

    fun FOR_NOT_CLOSED(template: String): String {
        return "'Foreach' not closed ${where(template)}"
    }

    fun CONTENT_AFTER_FOR_OPENING(template: String): String {
        return "Decoration content not allowed on the same line where foreach is opened ${where(template)}"
    }

    fun ONLY_COLLECTION_ALLOWED(template: String): String {
        return "Only collection data type allowed in foreach ${where(template)}"
    }

    fun COLLECTION_NOT_ALLOWED(template: String): String {
        return "Collection data type not allowed here ${where(template)}"
    }

    private fun where(who: String) = "from '$who.decoration'"

}