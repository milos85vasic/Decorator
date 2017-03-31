package net.milosvasic.tryout.decorator

import net.milosvasic.decorator.Decorator

fun main(args: Array<String>) {

    val decorator = Decorator()
    val html = decorator.decorate("index")

}