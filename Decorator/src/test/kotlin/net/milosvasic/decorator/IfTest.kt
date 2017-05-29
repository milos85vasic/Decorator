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
        Assert.assertTrue(lines[10].isEmpty())
        Assert.assertTrue(lines[11].isEmpty())
        Assert.assertEquals("s 3", lines[12])
        Assert.assertTrue(lines[13].isEmpty())
        Assert.assertTrue(lines[14].isEmpty())
        Assert.assertEquals("s 5", lines[15])
        Assert.assertTrue(lines[16].isEmpty())
        Assert.assertTrue(lines[17].isEmpty())
        Assert.assertEquals("x 1", lines[18])
        Assert.assertEquals("y 2", lines[19])
        Assert.assertEquals("x 3", lines[20])
        Assert.assertEquals("x 4", lines[21])
        Assert.assertTrue(lines[22].isEmpty())
        Assert.assertEquals("k 1", lines[23])
        Assert.assertTrue(lines[24].isEmpty())
        Assert.assertTrue(lines[25].isEmpty())
        Assert.assertEquals("k 3", lines[26])
        Assert.assertTrue(lines[27].isEmpty())
        Assert.assertTrue(lines[28].isEmpty())
        Assert.assertEquals("aaaccc", lines[29])
        Assert.assertEquals("aaaddd", lines[30])
        Assert.assertEquals("bbbccc", lines[31])
        Assert.assertEquals("bbbddd", lines[32])
        Assert.assertTrue(lines[33].isEmpty())

        Assert.assertEquals(134, lines.size)
    }

    @After
    fun afterTest() {
        logger.i(tag, "Template generated in $end ms.")
    }

}