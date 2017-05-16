package net.milosvasic.decorator

import net.milosvasic.decorator.data.DataBuilder
import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration
import org.junit.After
import org.junit.Assert
import org.junit.Test

class DecoratorTest {

    private var end = 0L
    private val tag = ""
    private val logger = SimpleLogger()

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

        val start = System.currentTimeMillis()
        val html = decorator.decorate("sample", data)
        end = System.currentTimeMillis() - start
        logger.v("", html)

        assertHtml(html)
    }

    fun assertHtml(html: String) {
        val lines = html.split("\n")
        Assert.assertFalse(lines.isEmpty())
        Assert.assertEquals("<!-- Template system: Decorator, https://github.com/milos85vasic/Decorator -->", lines[0])
        Assert.assertEquals("<!-- Template system version: ${BuildConfig.VERSION.replace("_", " ")} -->", lines[1])
    }

    @After
    fun afterTest() {
        logger.i(tag, "Template generated in $end ms.")
    }

}