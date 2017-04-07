package net.milosvasic.tryout.decorator

import net.milosvasic.decorator.Decorator
import net.milosvasic.decorator.data.Data
import net.milosvasic.decorator.data.Value
import net.milosvasic.logger.SimpleLogger

fun main(args: Array<String>) {
    val logger = SimpleLogger()
    val decorator = Decorator()
    val storage = Data()
    val data = Data()
    data.content["storage"] = storage
    data.content["mainTitle"] = Value("Trying out")
    storage.content["header"] = Value("Main header")
    storage.content["body"] = Value("Body paragraph ...")
    try {
        val html = decorator.decorate("sample", data)
        logger.i("", html)
    } catch (e: Exception){
        logger.e("", "Error: $e")
    }
}