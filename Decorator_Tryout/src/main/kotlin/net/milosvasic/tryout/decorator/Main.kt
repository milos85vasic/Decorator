package net.milosvasic.tryout.decorator

import net.milosvasic.decorator.Decorator
import net.milosvasic.logger.SimpleLogger

fun main(args: Array<String>) {
    val logger = SimpleLogger()
    val decorator = Decorator()
    val html = decorator.decorate("sample")
    logger.i("", html)

    val s = Something()

}

private class Something {

    val elze = Else()

    private class Else {
        fun doIt() {
            println("Do it.")
        }
    }

}