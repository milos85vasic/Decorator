package net.milosvasic.decorator.data

class DataBuilder {

    private val data = Data()

    fun append(key: String, value: String): DataBuilder {
        data.content[key] = Value(value)
        return this
    }

    fun append(key: String, value: Value): DataBuilder {
        data.content[key] = value
        return this
    }

    fun append(key: String, data: Data): DataBuilder {
        data.content[key] = data
        return this
    }

    fun append(key: String, builder: DataBuilder): DataBuilder {
        data.content[key] = builder.build()
        return this
    }

    fun append(key: String, collection: Collection): DataBuilder {
        data.content[key] = collection
        return this
    }

    fun build(): Data {
        return data
    }

}