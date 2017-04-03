package net.milosvasic.decorator.utils

object Text {

    fun isEmpty(text: String?): Boolean {
        return text == null || text.isEmpty()
    }

}