package net.milosvasic.tryout.decorator

import net.milosvasic.decorator.BuildConfig
import net.milosvasic.decorator.Decorator
import net.milosvasic.decorator.data.Data
import net.milosvasic.decorator.data.Value
import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration

fun main(args: Array<String>) {
    val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))
    val decorator = Decorator()
    val storage = Data()
    val data = Data()
    data.content["storage"] = storage
    data.content["credits"] = Value("Some guy...")
    data.content["mainTitle"] = Value("Trying out")
    data.content["footer"] = Value("- - - - - - -")
    storage.content["header"] = Value("Main header")
    storage.content["body"] = Value("Body paragraph ...")
    try {
        val html = decorator.decorate("sample", data)
        logger.v("", html)
    } catch (e: Exception) {
        logger.e("", "Error: $e")
        e.printStackTrace()
    }
}