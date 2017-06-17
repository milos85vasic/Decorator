package net.milosvasic.decorator

import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration
import org.junit.Test

class MainTest {

    private val tag = ""
    val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))

    @Test
    fun testDecoratorCompletely() {

    }

}