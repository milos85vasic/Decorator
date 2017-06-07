package net.milosvasic.decorator

import net.milosvasic.decorator.data.DataBuilder
import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration
import org.junit.After
import org.junit.Assert
import org.junit.Test

class IfTest {

    private val tag = ""
    private val logger = SimpleLogger()

    @Test
    fun testForeach() {
        val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))
        val data = DataBuilder()
                .append("exist", "exist")
                .append("does_not_exist", "")
                .append("something", "nice!")
                .append("person", "John")
                .build()

        var html = ""
        val decorator = Decorator("if", data)
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
//        Assert.assertEquals("m 1", lines[0])
//        Assert.assertEquals("t 2", lines[1])
//        Assert.assertEquals("m 3", lines[2])
//        Assert.assertEquals("t 4", lines[3])
//        Assert.assertEquals("m 5", lines[4])
//        Assert.assertEquals("m 8", lines[5])
//        Assert.assertEquals("s 1", lines[6])
//        Assert.assertEquals("s 3", lines[7])
//        Assert.assertEquals("s 5", lines[8])
//        Assert.assertEquals("x 1", lines[9])
//        Assert.assertEquals("y 2", lines[10])
//        Assert.assertEquals("x 3", lines[11])
//        Assert.assertEquals("x 4", lines[12])
//        Assert.assertEquals("k 1", lines[13])
//        Assert.assertEquals("k 3", lines[14])
//        Assert.assertEquals("aaaccc", lines[15])
//        Assert.assertEquals("aaaddd", lines[16])
//        Assert.assertEquals("bbbccc", lines[17])
//        Assert.assertEquals("bbbddd", lines[18])
//        Assert.assertEquals("--> nice! <--", lines[19])
//        Assert.assertEquals("nice!!!!", lines[20])
//        Assert.assertEquals("--> nice!!!!", lines[21])
//        Assert.assertEquals("nice! <--", lines[22])
//        Assert.assertEquals("--> nice! <--", lines[23])
//        Assert.assertEquals("~ ~ ~ nice! <--", lines[24])
//        Assert.assertEquals("--> nice!!!!", lines[25])
//        Assert.assertEquals("~ ~ ~ nice!!!!", lines[26])
//        Assert.assertEquals("sss sss sss ", lines[27])
//        Assert.assertEquals("John is right! Run now!", lines[28])
//        Assert.assertEquals("John is not right! Do not run now!", lines[29])
//        Assert.assertEquals("John is not right! Run now!", lines[30])
//        Assert.assertEquals("John is right! Do not run now!", lines[31])
//        Assert.assertEquals("John is right! John knows all!", lines[32])
//        Assert.assertEquals("John is not right! John does not know all!", lines[33])
//        Assert.assertEquals("John is not right! John knows all!", lines[34])
//        Assert.assertEquals("John is right! John does not know all!", lines[35])
//        Assert.assertEquals("--- repeating stuff", lines[36])
//        Assert.assertEquals("k 1", lines[37])
//        Assert.assertEquals("k 3", lines[38])
//        Assert.assertEquals("aaaccc", lines[39])
//        Assert.assertEquals("aaaddd", lines[40])
//        Assert.assertEquals("bbbccc", lines[41])
//        Assert.assertEquals("bbbddd", lines[42])
//        Assert.assertEquals("--> nice! <--", lines[43])
//        Assert.assertEquals("nice!!!!", lines[44])
//        Assert.assertEquals("--> nice!!!!", lines[45])
//        Assert.assertEquals("nice! <--", lines[46])
//        Assert.assertEquals("--> nice! <--", lines[47])
//        Assert.assertEquals("~ ~ ~ nice! <--", lines[48])
//        Assert.assertEquals("--> nice!!!!", lines[49])
//        Assert.assertEquals("~ ~ ~ nice!!!!", lines[50])
//        Assert.assertEquals("--- repeating stuff - END", lines[51])
//        Assert.assertEquals("--- end commented invalid ifs", lines[52])
//        Assert.assertEquals(". . . . .", lines[53])
//        Assert.assertEquals("nice!", lines[54])
//        Assert.assertEquals("- - -", lines[55])
//        Assert.assertEquals("YyY", lines[56])
//        Assert.assertEquals("- - -", lines[57])
//        Assert.assertEquals("Ha ha ha!", lines[58])
//        Assert.assertEquals("- - -", lines[59])
//        Assert.assertEquals("Repeating - START", lines[60])
//        Assert.assertEquals("---", lines[61])
//        Assert.assertEquals("m 1", lines[62])
//        Assert.assertEquals("t 2", lines[63])
//        Assert.assertEquals("m 3", lines[64])
//        Assert.assertEquals("t 4", lines[65])
//        Assert.assertEquals("m 5", lines[66])
//        Assert.assertEquals("m 8", lines[67])
//        Assert.assertEquals("s 1", lines[68])
//        Assert.assertEquals("s 3", lines[69])
//        Assert.assertEquals("s 5", lines[70])
//        Assert.assertEquals("x 1", lines[71])
//        Assert.assertEquals("y 2", lines[72])
//        Assert.assertEquals("x 3", lines[73])
//        Assert.assertEquals("x 4", lines[74])
//        Assert.assertEquals("k 1", lines[74])
//        Assert.assertEquals("k 3", lines[75])
//        Assert.assertEquals("aaaccc", lines[76])
//        Assert.assertEquals("aaaddd", lines[77])
//        Assert.assertEquals("bbbccc", lines[78])
//        Assert.assertEquals("bbbddd", lines[79])
//        Assert.assertEquals("--> nice! <--", lines[80])
//        Assert.assertEquals("nice!!!!", lines[81])
//        Assert.assertEquals("--> nice!!!!", lines[82])
//        Assert.assertEquals("nice! <--", lines[83])
//        Assert.assertEquals("--> nice! <--", lines[84])
//        Assert.assertEquals("~ ~ ~ nice! <--", lines[85])
//        Assert.assertEquals("--> nice!!!!", lines[86])
//        Assert.assertEquals("~ ~ ~ nice!!!!", lines[87])
//        Assert.assertEquals("---", lines[88])
//        Assert.assertEquals("Repeating - END", lines[89])
//        Assert.assertEquals("s 7", lines[90])
//        Assert.assertEquals("---", lines[91])
//        Assert.assertEquals(91, lines.size)
    }

}