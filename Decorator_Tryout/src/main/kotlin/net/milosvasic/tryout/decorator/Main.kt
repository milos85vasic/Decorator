package net.milosvasic.tryout.decorator

import net.milosvasic.decorator.Decorator
import net.milosvasic.logger.SimpleLogger

fun main(args: Array<String>) {

    val logger = SimpleLogger()

    val decorator = Decorator()
    val html = decorator.decorate("index")

    //logger.i("", html)

}