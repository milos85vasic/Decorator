package net.milosvasic.decorator.content

import net.milosvasic.decorator.DecoratorTags

object Messages {

    private val tags = DecoratorTags()

    val UNKNOWN_TEMPLATE_DATA_TYPE = "Unknown type passed."

    fun COULD_NOT_RESOLVE(line: String, template: String): String {
        return "Could not resolve '$line' ${where(template)}"
    }

    fun COLLECTION_NOT_ALLOWED(line: String, template: String): String {
        return "Collection not allowed here. '$line' ${where(template)}. Use 'foreach' iteration."
    }

    fun IF_NOT_OPENED(template: String): String {
        return "'If' not opened ${where(template)} Use ${tags.ifOpen}...${tags.ifClose} to open."
    }

    fun FOR_NOT_OPENED(template: String): String {
        return "'For' not opened ${where(template)}"
    }

    fun IF_NOT_CLOSED(template: String): String {
        return "'${tags.ifOpen}...${tags.ifClose}...' not closed ${where(template)} Use ${tags.endIf} to close."
    }

    fun IF_CONDITION_NOT_CLOSED(template: String): String {
        return "'${tags.ifOpen}' opened but not not closed ${where(template)} Use ${tags.ifClose} to close. Both must stand in the same row."
    }

    fun IF_CONDITION_NOT_OPENED(template: String): String {
        return "'${tags.ifClose}' closed but not not opened ${where(template)} Use ${tags.ifOpen} to open. Both must stand in the same row."
    }

    fun ELSE_NOT_CLOSED(template: String): String {
        return "'${tags.elseTag}' not closed ${where(template)} Use '${tags.endIf}' to close."
    }

    fun ELSE_VERTICAL_INVALID(template: String): String {
        return StringBuilder()
                .append("Vertical ${tags.elseTag} is not used correctly ${where(template)}")
                .append("\n")
                .append("When using ${tags.ifOpen}...${tags.ifClose}...${tags.elseTag} vertically ${tags.elseTag} must stand alone in row.")
                .toString()
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

    fun INVALID_INDEX(key: String, size: Int) : String {
        return "No results for '$key', size is: $size"
    }

    private fun where(who: String) = "from '$who' template."

}