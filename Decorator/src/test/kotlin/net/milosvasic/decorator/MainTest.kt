package net.milosvasic.decorator

import net.milosvasic.decorator.data.data
import net.milosvasic.logger.SimpleLogger
import net.milosvasic.logger.VariantsConfiguration
import org.junit.Test

class MainTest {

    private val tag = ""
    val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))

    @Test
    fun testDecoratorCompletely() {
        val data = data()
                .append("company", "Umbrella Corporation")
                .append("enterprise", "")
                .append(
                        "sections",
                        data()
                                .append("main", "Main section")
                                .append("floor 1", "Floor 1")
                                .append("floor 2", "Floor 2")
                                .append("floor 3", "")
                                .build()
                )
                .append("skills", listOf("social", "engineer", "cleaner", "cook"))
                .append(
                        "subskills",
                        data()
                                .append("social", listOf("talking", "leading"))
                                .build()
                )
                .append(
                        "subskills",
                        data()
                                .append("social", listOf("talking shadowed", "leading shadowed"))
                                .build()
                )
                .append(
                        "education_levels",
                        data()
                                .append("high_school", "")
                                .build()
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
                                        .build(),
                                data()
                                        .append("firstName", "John")
                                        .append("lastName", "Smith 2nd")
                                        .append(
                                                "skills",
                                                data()
                                                        .append("social", "fair")
                                                        .append("engineer", "excelent")
                                        )
                                        .build()
                        )
                )
                .append("coworkers", data().build())
                .build()


    }

}