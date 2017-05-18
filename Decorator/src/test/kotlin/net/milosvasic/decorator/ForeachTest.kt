package net.milosvasic.decorator

import net.milosvasic.decorator.data.DataBuilder
import net.milosvasic.decorator.data.data
import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration
import org.junit.After
import org.junit.Assert
import org.junit.Test

class ForeachTest {

    private var end = 0L
    private val tag = ""
    private val logger = SimpleLogger()

    @Test
    fun testForeach() {
        val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))
        val decorator = Decorator()
        val data = DataBuilder()
                .append("something", "nice!")
                .append(
                        "storage",
                        DataBuilder()
                                .append("header", "Main header")
                                .append("body", "Body paragraph ...")
                                .append("stuff", listOf("Xxx", "Yyy", "Zzz"))
                )
                .append(
                        "names",
                        listOf("Milos", "Maja")
                )
                .append(
                        "cars",
                        listOf(
                                data().get("name", "Fiat"),
                                data().get("name", "BMW"),
                                data().get("name", "Mercedes"),
                                data().get("name", "Ford"),
                                data().get("name", "Golf"),
                                data().get("name", "Audi"),
                                data().get("name", "Citroen"),
                                data().get("name", "Zastava"),
                                data().get("name", "Kia")
                        )
                )
                .build()

        val start = System.currentTimeMillis()
        val html = decorator.decorate("foreach", data)
        end = System.currentTimeMillis() - start
        logger.v("", html)

        assertHtml(html)
    }

    fun assertHtml(html: String) {
        val lines = mutableListOf<String>()
        lines.addAll(html.split("\n"))
        Assert.assertFalse(lines.isEmpty())
        lines.removeAt(lines.lastIndex)
        Assert.assertFalse(lines.isEmpty())

        // Assert.assertEquals(34, lines.size)
    }

    @After
    fun afterTest() {
        logger.i(tag, "Template generated in $end ms.")
    }

}