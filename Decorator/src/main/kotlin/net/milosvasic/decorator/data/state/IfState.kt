package net.milosvasic.decorator.data.state

class IfState(from: Pair<Int, Int>, to: Pair<Int, Int>, var value: Boolean = true) : ConditionState(from, to)