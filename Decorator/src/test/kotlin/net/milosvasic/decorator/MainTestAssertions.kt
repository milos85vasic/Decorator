package net.milosvasic.decorator

object MainTestAssertions {

    val positiveAssertions = listOf(

"""!-- Template system: Decorator, https://github.com/milos85vasic/Decorator -->
<!-- Template system version: 1.0.0 Alpha 1 DEV 1495451759151 -->
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
  <title>Main Decorator test</title>
</head>
<body>
<h1>Umbrella Corporation</h1>""",

"""</body>
</html>""",

"""<p>If 1 ok.</p>""",

"""<p>If 2 ok.</p>""",

"""If 4 not ok.""",

"""<p>If 6 not ok.</p>""",

"""<p>If 7 ok.</p>""",

"""<p>If 8 ok.</p>""",

"""<p>If 1a ok.</p>""",

"""<p>If 2a ok.</p>""",

"""If 4a not ok.""",

"""<p>If 6a not ok.</p>""",

"""<p>If 7a ok.</p>""",

"""<p>If 8a ok.</p>""",

"""        <p>If 1b ok.</p>"""

    )

    val negativeAssertions = listOf(

"""If 2 not ok.""",

"""<p>If 3 ok.</p>""",

"""<p>If 4 ok.</p>""",

"""<p>If 5 ok.</p>""",

"""<p>If 6 ok.</p>""",

"""<p>If 8 not ok.</p>""",

"""If 2a not ok.""",

"""<p>If 3a ok.</p>""",

"""<p>If 4a ok.</p>""",

"""<p>If 5a ok.</p>""",

"""<p>If 6a ok.</p>""",

"""<p>If 8a not ok.</p>""",

"""        <p>If 1b2 ok.</p>""",

"""        <p>If 1b3 ok.</p>"""

    )

}