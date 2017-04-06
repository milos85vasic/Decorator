package net.milosvasic.tryout.decorator

import net.milosvasic.decorator.Decorator
import net.milosvasic.decorator.data.Data
import net.milosvasic.decorator.data.Value
import net.milosvasic.logger.SimpleLogger

fun main(args: Array<String>) {
    val logger = SimpleLogger()
    val decorator = Decorator()
    val data = Data()
    data.content["mainTitle"] = Value("Trying out")
    val html = decorator.decorate("sample", data)
    logger.i("", html)
}