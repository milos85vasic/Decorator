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
        Assert.assertEquals("--> nice! <--", lines[34])
        Assert.assertEquals("nice!!!!", lines[35])
        Assert.assertEquals("--> nice!!!!", lines[36])
        Assert.assertEquals("nice! <--", lines[37])
        Assert.assertEquals("--> nice! <--", lines[38])
        Assert.assertEquals("~ ~ ~ nice! <--", lines[39])
        Assert.assertEquals("--> nice!!!!", lines[40])
        Assert.assertEquals("~ ~ ~ nice!!!!", lines[41])
        Assert.assertTrue(lines[42].isEmpty())
        Assert.assertEquals("sss sss sss ", lines[43])
        Assert.assertTrue(lines[44].isEmpty())
        Assert.assertEquals("John is right! Run now!", lines[45])
        Assert.assertEquals("John is not right! Do not run now!", lines[46])
        Assert.assertEquals("John is not right! Run now!", lines[47])
        Assert.assertEquals("John is right! Do not run now!", lines[48])
        Assert.assertTrue(lines[49].isEmpty())
        Assert.assertEquals("John is right! John knows all!", lines[50])
        Assert.assertEquals("John is not right! John does not know all!", lines[51])
        Assert.assertEquals("John is not right! John knows all!", lines[52])
        Assert.assertEquals("John is right! John does not know all!", lines[53])
        Assert.assertTrue(lines[54].isEmpty())
        Assert.assertEquals("--- repeating stuff", lines[55])
        Assert.assertEquals("k 1", lines[56])
        Assert.assertTrue(lines[57].isEmpty())
        Assert.assertTrue(lines[58].isEmpty())
        Assert.assertEquals("k 3", lines[59])
        Assert.assertTrue(lines[60].isEmpty())
        Assert.assertTrue(lines[61].isEmpty())
        Assert.assertEquals("aaaccc", lines[62])
        Assert.assertEquals("aaaddd", lines[63])
        Assert.assertEquals("bbbccc", lines[64])
        Assert.assertEquals("bbbddd", lines[65])
        Assert.assertTrue(lines[66].isEmpty())
        Assert.assertEquals("--> nice! <--", lines[67])
        Assert.assertEquals("nice!!!!", lines[68])
        Assert.assertEquals("--> nice!!!!", lines[69])
        Assert.assertEquals("nice! <--", lines[70])
        Assert.assertEquals("--> nice! <--", lines[71])
        Assert.assertEquals("~ ~ ~ nice! <--", lines[72])
        Assert.assertEquals("--> nice!!!!", lines[73])
        Assert.assertEquals("~ ~ ~ nice!!!!", lines[74])
        Assert.assertEquals("--- repeating stuff - END", lines[75])


        Assert.assertEquals(134, lines.size)
    }

    @After
    fun afterTest() {
        logger.i(tag, "Template generated in $end ms.")
    }

}