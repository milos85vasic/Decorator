package net.milosvasic.decorator.data

class Value(val content: String) : TemplateData() {

    constructor(value: Int) : this(value.toString())

    constructor(value: Long) : this(value.toString())

    constructor(value: Short) : this(value.toString())

    constructor(value: Float) : this(value.toString())

    constructor(value: Double) : this(value.toString())

}