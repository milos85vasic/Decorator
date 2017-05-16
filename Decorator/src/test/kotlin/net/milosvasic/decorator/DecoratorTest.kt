package net.milosvasic.decorator

import net.milosvasic.decorator.data.DataBuilder
import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration
import org.junit.Test

class DecoratorTest {

    @Test
    fun testDecorator() {
        val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))
        val decorator = Decorator()
        val data = DataBuilder()
                .append("doesNotExist", "")
                .append("credits", "Some guy...")
                .append("mainTitle", "Trying out")
                .append("footer", "- - - - - - -")
                .append(
                        "storage",
                        DataBuilder()
                                .append("header", "Main header")
                                .append("body", "Body paragraph ...")
                )
                .build()
        try {
            val html = decorator.decorate("sample", data)
            logger.v("", html)
        } catch (e: Exception) {
            logger.e("", "Error: $e")
            e.printStackTrace()
        }
    }

}