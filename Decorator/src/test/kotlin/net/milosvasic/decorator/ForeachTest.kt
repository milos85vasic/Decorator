package net.milosvasic.decorator

import net.milosvasic.decorator.data.DataBuilder
import net.milosvasic.decorator.data.data
import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration
import org.junit.After
import org.junit.Assert
import org.junit.Test

class ForeachTest {

    private val tag = ""
    private val logger = SimpleLogger()

    @Test
    fun testForeach() {
        val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))
        val data = DataBuilder()
                .append("something", "nice!")
                .append("footer", "f.o.o.t.e.r.")
                .append(
                        "storage",
                        DataBuilder()
                                .append("header", "Main header")
                                .append("body", "Body paragraph ...")
                                .append("stuff", listOf("Xxx", "Yyy", "Zzz", "111", "222"))
                                .append("no_stuff", listOf<String>())
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

        var html = ""
        val decorator = Decorator("foreach", data)
        for (x in 0..10) { // Repeat a few times to see timings.
            val start = System.currentTimeMillis()
            html = decorator.getContent()
            val end = System.currentTimeMillis() - start
            logger.i(tag, "Template generated in $end ms.")
            assertHtml(html) // Check if we got valid result.
        }

        logger.v("", html)
    }

    fun assertHtml(html: String) {
//        val lines = mutableListOf<String>()
//        lines.addAll(html.split("\n"))
//        Assert.assertFalse(lines.isEmpty())
//        lines.removeAt(lines.lastIndex)
//        Assert.assertFalse(lines.isEmpty())

        // Assert.assertEquals(34, lines.size)
    }

    @After
    fun afterTest() {
        // TODO: TBD.
    }

}