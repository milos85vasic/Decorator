package net.milosvasic.tryout.decorator

import net.milosvasic.decorator.DecoratorImpl

fun main(args: Array<String>) {

    val decorator = DecoratorImpl()
    val html = decorator.decorate("index")
    println(html)

}