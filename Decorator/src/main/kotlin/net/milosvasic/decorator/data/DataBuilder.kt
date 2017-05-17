package net.milosvasic.decorator.data

class DataBuilder : DataBuilderAbstract() {

    private val data = Data()

    override fun append(key: String, value: String): DataBuilder {
        data.content[key] = Value(value)
        return this
    }

    override fun append(key: String, value: Value): DataBuilder {
        data.content[key] = value
        return this
    }

    override fun append(key: String, data: Data): DataBuilder {
        data.content[key] = data
        return this
    }

    override fun append(key: String, builder: DataBuilder): DataBuilder {
        data.content[key] = builder.build()
        return this
    }

    override fun append(key: String, collection: Collection): DataBuilder {
        data.content[key] = collection
        return this
    }

    override fun append(key: String, iterable: Iterable<TemplateData>): DataBuilder {
        data.content[key] = Collection(iterable)
        return this
    }

    override fun append(key: String, iterable: List<String>): DataBuilder {
        convert(iterable, key)
        return this
    }

    override fun append(key: String, iterable: Set<String>): DataBuilder {
        convert(iterable, key)
        return this
    }

    override fun build(): Data {
        return data
    }

    private fun convert(iterable: Iterable<String>, key: String) {
        val items = mutableListOf<Value>()
        iterable.forEach {
            item ->
            items.add(Value(item))
        }
        data.content[key] = Collection(items)
    }

}