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
    val logger = SimpleLogger(VariantsConfiguration(BuildConfig.VARIANT, listOf("DEV")))

    @Test
    fun testForeach() {
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
                .append(
                        "misc",
                        listOf("First", "Second", "Third")
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
        val assertionItems = listOf(

"""---
Xxx
Zzz
---""",


"""<ul>
 dd
    <li>
        <a href="/0">Item [ 0 ][ Xxx ]</a>
    </li>
 dd
    <li>
        <a href="/1">Item [ 1 ][ Yyy ]</a>
    </li>
 dd
    <li>
        <a href="/2">Item [ 2 ][ Zzz ]</a>
    </li>
 dd
    <li>
        <a href="/3">Item [ 3 ][ 111 ]</a>
    </li>
 dd
    <li>
        <a href="/4">Item [ 4 ][ 222 ]</a>
    </li>
</ul>
---""",


"""<p>
Name: John
Last name: Smith
</p>""",

"""<ul>  ok
    <li>
        <a href="/0">
            Index [ 0 ]
            Item [ First ]
        </a>
    </li>
    <li>
        <a href="/1">
            Index [ 1 ]
            Item [ Second ]
        </a>
    </li>
    <li>
        <a href="/2">
            Index [ 2 ]
            Item [ Third ]
        </a>
    </li>
</ul>
---""",


"""<ul>  ok
    <li>
        <a href="/0">
            Worker:
            Index [ 0 ]
            First name: [ John ]
            Last name: [ Smith ]
        </a>
    </li>
    <li>
        <a href="/1">
            Worker:
            Index [ 1 ]
            First name: [ Pera ]
            Last name: [ Peric ]
        </a>
    </li>
    <li>
        <a href="/2">
            Worker:
            Index [ 2 ]
            First name: [ Unknown ]
            Last name: [ - - - ]
        </a>
    </li>
</ul>""",

"""---
<ul>
    <li>
        <a href="/">
            0
            00000
            0
            0 dsdsds 0 kssksks 0 dkdd
        </a>
    </li>
    <li>
        <a href="/">
            1
            11111
            1
            1 dsdsds 1 kssksks 1 dkdd
        </a>
    </li>
    <li>
        <a href="/">
            2
            22222
            2
            2 dsdsds 2 kssksks 2 dkdd
        </a>
    </li>
</ul>
---""",

"""<ul>
 dd
    <li>
        <a href="/0">Item [ 0 ][ Xxx ]</a>
    </li>
 dd
    <li>
        <a href="/1">Item [ 1 ][ Yyy ]</a>
    </li>
 dd
    <li>
        <a href="/2">Item [ 2 ][ Zzz ]</a>
    </li>
 dd
    <li>
        <a href="/3">Item [ 3 ][ 111 ]</a>
    </li>
 dd
    <li>
        <a href="/4">Item [ 4 ][ 222 ]</a>
    </li>
</ul>
---
<ul>
</ul>
---""",

"""<p>Footer text: f.o.o.t.e.r.</p>
---
<ul>
    <li>
        <p>Footer text: f.o.o.t.e.r.</p>
    </li>
    <li>
        <p>Footer text: f.o.o.t.e.r.</p>
    </li>
    <li>
        <p>Footer text: f.o.o.t.e.r.</p>
    </li>
    <li>
        <p>Footer text: f.o.o.t.e.r.</p>
    </li>
    <li>
        <p>Footer text: f.o.o.t.e.r.</p>
    </li>
</ul>
---""",

"""<ul>
    <li>
        <a href="/">
            Worker:
            Index [ 0 ]
            First name: [ John ]
            Last name: [ Smith ]
            Status: [ nice! ]
            Social skills: [ excellent ]
        </a>
    </li>
    <li>
        <a href="/">
            Worker:
            Index [ 1 ]
            First name: [ Pera ]
            Last name: [ Peric ]
            Status: [ nice! ]
            Social skills: [ - - - ]
        </a>
    </li>
    <li>
        <a href="/">
            Worker:
            Index [ 2 ]
            First name: [ Unknown ]
            Last name: [ - - - ]
            Status: [ nice! ]
            Social skills: [ - - - ]
        </a>
    </li>
</ul>
---""",

"""--- latest stuff
<ul>
    <li>Item [ 0 ][ Milos ]</li>
    <li>Item [ 1 ][ Maja ]</li>
</ul>
...""",

"""<ul>
    <li>Item [ 0 ][ Fiat ]</li>
    <li>Item [ 1 ][ BMW ]</li>
    <li>Item [ 2 ][ Mercedes ]</li>
    <li>Item [ 3 ][ Ford ]</li>
    <li>Item [ 4 ][ Golf ]</li>
    <li>Item [ 5 ][ Audi ]</li>
    <li>Item [ 6 ][ Citroen ]</li>
    <li>Item [ 7 ][ Zastava ]</li>
    <li>Item [ 8 ][ Kia ]</li>
</ul>
...""",

"""<ul>
    <li>
        <a href="/0">Item [ 0 ][ Xxx ]</a>
    </li>
    <li>
        <a href="/1">Item [ 1 ][ Yyy ]</a>
    </li>
    <li>
        <a href="/2">Item [ 2 ][ Zzz ]</a>
    </li>
    <li>
        <a href="/3">Item [ 3 ][ 111 ]</a>
    </li>
    <li>
        <a href="/4">Item [ 4 ][ 222 ]</a>
    </li>
</ul>
...""",

"""<ul>
    <li>
        <a href="/0">Item [ 0 ][ Xxx ][ nice! ]</a>
    </li>
    <li>
        <a href="/1">Item [ 1 ][ Yyy ][ nice! ]</a>
    </li>
    <li>
        <a href="/2">Item [ 2 ][ Zzz ][ nice! ]</a>
    </li>
    <li>
        <a href="/3">Item [ 3 ][ 111 ][ nice! ]</a>
    </li>
    <li>
        <a href="/4">Item [ 4 ][ 222 ][ nice! ]</a>
    </li>
</ul>
...""",

"""<ul>
    <li>
        <a href="/0">Worker [ nice! ]</a>
    </li>
    <li>
        <a href="/1">Worker [ nice! ]</a>
    </li>
    <li>
        <a href="/2">Worker [ nice! ]</a>
    </li>
</ul>
...""",

"""<ul>
    <li>
        <a href="/0>">Worker [ 0 ][ John ][ Smith ][ nice! ][ excellent ]</a>
    </li>
    <li>
        <a href="/1>">Worker [ 1 ][ Pera ][ Peric ][ nice! ][  ]</a>
    </li>
    <li>
        <a href="/2>">Worker [ 2 ][ Unknown ][  ][ nice! ][  ]</a>
    </li>
</ul>
...""",


"""<ul>
    <li>
        <a href="/0">
            Worker:
            Index [ 0 ]
            First name: [ John ]
            Last name: [ Smith ]
            Status: [ nice! ]
            Social skills: [ excellent ]
        </a>
    </li>
    <li>
        <a href="/1">
            Worker:
            Index [ 1 ]
            First name: [ Pera ]
            Last name: [ Peric ]
            Status: [ nice! ]
            Social skills: [ - - - ]
        </a>
    </li>
    <li>
        <a href="/2">
            Worker:
            Index [ 2 ]
            First name: [ Unknown ]
            Last name: [ - - - ]
            Status: [ nice! ]
            Social skills: [ - - - ]
        </a>
    </li>
</ul>"""


)

        assertionItems.forEach {
            item ->
            Assert.assertTrue(item in html)
        }
    }

}