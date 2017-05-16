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
                .append("mainTitle", "This is decorator test")
                .append("footer", "- f o o t e r -")
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
        Assert.assertTrue(lines[9].isEmpty())
        Assert.assertEquals("<h1>This is: Main header</h1>", lines[10])
        Assert.assertEquals("<h2>This is: Main header with title assigned: This is decorator test</h2>", lines[11])
        Assert.assertEquals("<p>Body paragraph ...</p>", lines[12])
        Assert.assertTrue(lines[13].isEmpty())
        Assert.assertEquals("<h2>Credits:</h2>", lines[14])
        Assert.assertEquals("<p>Some guy...</p>", lines[15])
        Assert.assertTrue(lines[16].isEmpty())
        Assert.assertEquals("<p>Footer text: - f o o t e r -</p>", lines[17])
        Assert.assertEquals("After footer: XXX1 AND XXX2", lines[18])
        Assert.assertTrue(lines[19].isEmpty())
        Assert.assertEquals("<p>We have main title!</p>", lines[20])
        Assert.assertTrue(lines[21].isEmpty())
        Assert.assertTrue(lines[22].isEmpty())
        Assert.assertEquals("<p>We have main title and footer!</p>", lines[23])
        Assert.assertEquals("<p>We have main title and footer!</p>", lines[24])
        Assert.assertEquals("<p>We have main title and footer!</p>", lines[25])
        Assert.assertTrue(lines[26].isEmpty())
        Assert.assertTrue(lines[27].isEmpty())
        Assert.assertEquals("<p>Negation works.</p>", lines[28])
        Assert.assertTrue(lines[29].isEmpty())
        Assert.assertEquals(32, lines.size)
        Assert.assertEquals("</body>", lines[lines.lastIndex - 1])
        Assert.assertEquals("</html>", lines.last())
    }

    @After
    fun afterTest() {
        logger.i(tag, "Template generated in $end ms.")
    }

}