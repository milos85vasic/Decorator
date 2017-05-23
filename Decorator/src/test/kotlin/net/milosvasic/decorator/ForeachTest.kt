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
                        "workers",
                        listOf(
                                data()
                                        .append("firstName", "John")
                                        .append("lastName", "Smith")
                                        .append(
                                                "skills",
                                                data().append(
                                                        "social", "excellent"
                                                )
                                        )
                                        .build(),
                                data()
                                        .append("firstName", "Pera")
                                        .append("lastName", "Peric")
                                        .build(),
                                data()
                                        .append("firstName", "Unknown")
                                        .build()
                        )
                )
                .append(
                        "names",
                        listOf("Milos", "Maja")
                )
                .append(
                        "cars",
                        listOf(
                                data().get("model", "Fiat"),
                                data().get("model", "BMW"),
                                data().get("model", "Mercedes"),
                                data().get("model", "Ford"),
                                data().get("model", "Golf"),
                                data().get("model", "Audi"),
                                data().get("model", "Citroen"),
                                data().get("model", "Zastava"),
                                data().get("model", "Kia")
                        )
                )
                .build()

        val start = System.currentTimeMillis()
        val html = decorator.decorate("if_horizontal", data)
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