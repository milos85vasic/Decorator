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

    fun get(key: String, value: String): Data {
        return DataBuilder().append(key, value).build()
    }

    fun get(key: String, value: Value): Data {
        return DataBuilder().append(key, value).build()
    }

    fun get(key: String, data: Data): Data {
        return DataBuilder().append(key, data).build()
    }

    fun get(key: String, builder: DataBuilder): Data {
        return DataBuilder().append(key, builder).build()
    }

    fun get(key: String, collection: Collection): Data {
        return DataBuilder().append(key, collection).build()
    }

    fun get(key: String, iterable: Iterable<TemplateData>): Data {
        return DataBuilder().append(key, iterable).build()
    }

    fun get(key: String, iterable: List<String>): Data {
        return DataBuilder().append(key, iterable).build()
    }

    fun get(key: String, iterable: Set<String>): Data {
        return DataBuilder().append(key, iterable).build()
    }

}

fun data(): DataBuilderWrapper {
    return DataBuilderWrapper()
}

