package net.milosvasic.decorator.data

abstract class DataBuilderAbstract {

    abstract fun append(key: String, value: String): DataBuilderAbstract

    abstract fun append(key: String, value: Value): DataBuilderAbstract

    abstract fun append(key: String, data: Data): DataBuilderAbstract

    abstract fun append(key: String, builder: DataBuilder): DataBuilderAbstract

    abstract fun append(key: String, collection: Collection): DataBuilderAbstract

    abstract fun append(key: String, iterable: Iterable<TemplateData>): DataBuilderAbstract

    abstract fun append(key: String, iterable: List<String>): DataBuilderAbstract

    abstract fun append(key: String, iterable: Set<String>): DataBuilderAbstract

    abstract fun build(): Data

}