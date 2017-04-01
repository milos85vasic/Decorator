package net.milosvasic.tryout.decorator

import net.milosvasic.decorator.DecoratorImpl
import net.milosvasic.logger.SimpleLogger

fun main(args: Array<String>) {

    val logger = SimpleLogger()

    val decorator = DecoratorImpl()
    val html = decorator.decorate("index")

    logger.i("", html)

}