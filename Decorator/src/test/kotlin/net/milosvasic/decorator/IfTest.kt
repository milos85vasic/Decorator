package net.milosvasic.decorator

import net.milosvasic.decorator.data.DataBuilder
import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration
import org.junit.After
import org.junit.Assert
import org.junit.Test

class IfTest {

    private var end = 0L
    private val tag = ""
    private val logger = SimpleLogger()

    @Test
    fun testForeach() {
        val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))
        val decorator = Decorator()
        val data = DataBuilder()
                .append("exist", "exist")
                .append("does_not_exist", "")
                .append("something", "nice!")
                .append("person", "John")
                .build()

        val start = System.currentTimeMillis()
        val html = decorator.decorate("if", data)
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
        Assert.assertEquals("m 1", lines[0])
        Assert.assertEquals("t 2", lines[1])
        Assert.assertEquals("m 3", lines[2])
        Assert.assertEquals("t 4", lines[3])
        Assert.assertEquals("m 5", lines[4])
        Assert.assertTrue(lines[5].isEmpty())
        Assert.assertTrue(lines[6].isEmpty())
        Assert.assertEquals("m 8", lines[7])
        Assert.assertTrue(lines[8].isEmpty())
        Assert.assertEquals("s 1", lines[9])
        Assert.assertEquals(134, lines.size)
    }

    @After
    fun afterTest() {
        logger.i(tag, "Template generated in $end ms.")
    }

}