package net.milosvasic.decorator.data.state

class IfState(from: Int, to: Int, var value: Boolean = true) : ConditionState(from, to)