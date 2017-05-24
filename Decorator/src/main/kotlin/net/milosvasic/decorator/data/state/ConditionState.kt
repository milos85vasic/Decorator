package net.milosvasic.decorator.data.state

abstract class ConditionState(var from: Pair<Int, Int>, var to: Pair<Int, Int> = Pair(-1, -1))