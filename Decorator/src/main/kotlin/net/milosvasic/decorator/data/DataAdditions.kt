package net.milosvasic.decorator.data

class DataBuilderWrapper : DataBuilderAbstract() {

    private val builder = DataBuilder()

    override fun append(key: String, value: String): DataBuilder {
        return builder.append(key, value)
    }

    override fun append(key: String, value: Value): DataBuilder {
        return builder.append(key, value)
    }

    override fun append(key: String, data: Data): DataBuilder {
        return builder.append(key, data)
    }

    override fun append(key: String, builder: DataBuilder): DataBuilder {
        return builder.append(key, builder)
    }

    override fun append(key: String, collection: Collection): DataBuilder {
        return builder.append(key, collection)
    }

    override fun append(key: String, iterable: Iterable<TemplateData>): DataBuilder {
        return builder.append(key, iterable)
    }

    override fun append(key: String, iterable: List<String>): DataBuilder {
        return builder.append(key, iterable)
    }

    override fun append(key: String, iterable: Set<String>): DataBuilder {
        return builder.append(key, iterable)
    }

    override fun build(): Data {
        return builder.build()
    }

}

fun data(): DataBuilderWrapper {
    return DataBuilderWrapper()
}

