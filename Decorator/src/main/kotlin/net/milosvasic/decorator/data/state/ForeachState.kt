package net.milosvasic.decorator.data.state

class ForeachState(from: Pair<Int, Int>, to: Pair<Int, Int>, var value: String) : ConditionState(from, to)