package net.milosvasic.decorator

import net.milosvasic.decorator.data.DataBuilder
import net.milosvasic.decorator.data.data
import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration
import org.junit.After
import org.junit.Assert
import org.junit.Test

class DecoratorTest {

    private val tag = ""
    private val logger = SimpleLogger()

    @Test
    fun testDecorator() {
        val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))
        val data = DataBuilder()
                .append("doesNotExist", "")
                .append("doesExist", "exist!")
                .append("credits", "Some guy...")
                .append("mainTitle", "This is decorator test")
                .append("footer", "- f o o t e r -")
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

        var html = ""
        val decorator = Decorator("sample", data)
        for (x in 0..10) {
            val start = System.currentTimeMillis()
            html = decorator.getContent()
            val end = System.currentTimeMillis() - start
            logger.i(tag, "Template generated in $end ms.")
            assertHtml(html)
        }

        logger.v("", html)
    }

    fun assertHtml(html: String) {
        val lines = mutableListOf<String>()
        lines.addAll(html.split("\n"))
        Assert.assertFalse(lines.isEmpty())
        lines.removeAt(lines.lastIndex)
        Assert.assertFalse(lines.isEmpty())
        Assert.assertEquals("<!-- Template system: Decorator, https://github.com/milos85vasic/Decorator -->", lines[0])
        Assert.assertEquals("<!-- Template system version: ${BuildConfig.VERSION.replace("_", " ")} -->", lines[1])
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>", lines[2])
        Assert.assertEquals("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">", lines[3])
        Assert.assertEquals("<html xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\">", lines[4])
        Assert.assertEquals("<head>", lines[5])
        Assert.assertEquals("<title>This is decorator test</title>", lines[6])
        Assert.assertEquals("</head>", lines[7])
        Assert.assertEquals("<body>", lines[8])
        Assert.assertEquals("<h1>This is: Main header</h1>", lines[9])
        Assert.assertEquals("<h2>This is: Main header with title assigned: This is decorator test</h2>", lines[10])
        Assert.assertEquals("<p>Body paragraph ...</p>", lines[11])
        Assert.assertEquals("<h2>Credits:</h2>", lines[12])
        Assert.assertEquals("<p>Some guy...</p>", lines[13])
        Assert.assertEquals("<p>Footer text: - f o o t e r -</p>", lines[14])
        Assert.assertEquals("After footer: XXX1 AND XXX2", lines[15])
        Assert.assertEquals("<p>We have main title!</p>", lines[16])
        Assert.assertEquals("<p>We have main title and footer!</p>", lines[17])
        Assert.assertEquals("<p>We have main title and footer!</p>", lines[18])
        Assert.assertEquals("<p>We have main title and footer!</p>", lines[19])
        Assert.assertEquals("<p>Negation works.</p>", lines[20])
        Assert.assertEquals("<p>Will show - else</p>", lines[21])
        Assert.assertEquals("Some text 1 ", lines[22])
        Assert.assertEquals("Some text 2", lines[23])
        Assert.assertEquals("Some text 3 ", lines[24])
        Assert.assertEquals("Some text 4", lines[25])
        Assert.assertEquals("Some text 5 ", lines[26])
        Assert.assertEquals("Some text 6", lines[27])
        Assert.assertEquals("Some text 7", lines[28])
        Assert.assertEquals("Tab placeholder trimmed", lines[29])
        Assert.assertEquals("<p>Will show</p>", lines[30])
        Assert.assertEquals("</body>", lines[31])
        Assert.assertEquals("</html>", lines[32])
        Assert.assertEquals(32, lines.size)
    }

}