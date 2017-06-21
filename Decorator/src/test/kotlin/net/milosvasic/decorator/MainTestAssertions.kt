package net.milosvasic.decorator

object MainTestAssertions {

    private val tags = DecoratorTags()

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

"""        <p>If 1b ok.</p>""",

"""        <p>If 2 ok.</p>""",

"""    <p>If 2k ok.</p>""",

"""        <p>If 2l ok.</p>""",

"""    <p>If 2b2 not ok.</p>""",

"""        <p>If 2c2 ok.</p>""",

"""        <p>If 2d2 ok.</p>""",

"""    <p>If 2e2 not ok.</p>""",

"""        <p>If 2f2 not ok.</p>""",

"""        <p>If 2g2 not ok.</p>""",

"""        <p>If 2h not ok.</p>""",

"""    <p>If 2i ok.</p>""",

"""        <p>If 2j not ok.</p>""",

"""<p>If 7b ok.</p>""",

"""        <p>If 8b not ok.</p>""",

"""            We have company!
                And the rest of text...""",

"""x1""",

"""x2""",

"""                            We have company! B
                                And the rest of text... B""",

"""a-1""",

"""a2- else""",

"""a-3""",

"""a-4""",

"""a-5""",

"""a-6""",

"""a7- else""",

"""a-8""",

"""        <p>@@@ If 1b ok.</p>""",

"""        <p>@@@ If 2 ok.</p>""",

"""    <p>@@@ If 2k ok.</p>""",

"""        <p>@@@ If 2l ok.</p>""",

"""    <p>@@@ If 2b2 not ok.</p>""",

"""        <p>@@@ If 2c2 ok.</p>""",

"""        <p>@@@ If 2d2 ok.</p>""",

"""    <p>@@@ If 2e2 not ok.</p>""",

"""        <p>@@@ If 2f2 not ok.</p>""",

"""        <p>@@@ If 2g2 not ok.</p>""",

"""        <p>@@@ If 2h not ok.</p>""",

"""    <p>@@@ If 2i ok.</p>""",

"""        <p>@@@ If 2j not ok.</p>""",

"""<p>@@@ If 7b ok.</p>""",

"""        <p>@@@ If 8b not ok.</p>""",

"""            @@@ We have company!
                @@@ And the rest of text...""",

"""x1""",

"""x2""",

"""                            @@@ We have company! B
                                @@@ And the rest of text... B""",

"""@@@ a-1""",

"""@@@ a2- else""",

"""@@@ a-3""",

"""@@@ a-4""",

"""@@@ a-5""",

"""@@@ a-6""",

"""@@@ a7- else""",

"""@@@ a-8"""

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

"""        <p>If 1b3 ok.</p>""",

"""        <p>If 2 not ok.</p>""",

"""    <p>If 2 not not ok.</p>""",

"""        <p>If 2k2 ok.</p>""",

"""        <p>If 2k not ok.</p>""",

"""        <p>If 2l not ok.</p>""",

"""        <p>If 2l2 ok.</p>""",

"""        <p>If 2l2 not ok.</p>""",

"""        <p>If 2b ok.</p>""",

"""        <p>If 2b not ok.</p>""",

"""    <p>If 2c ok.</p>""",

"""        <p>If 2c not ok.</p>""",

"""        <p>If 2d ok.</p>""",

"""        <p>If 2d not ok.</p>""",

"""        <p>If 2d2 not ok.</p>""",

"""        <p>If 2e ok.</p>""",

"""        <p>If 2e not ok.</p>""",

"""    <p>If 2f ok.</p>""",

"""        <p>If 2f2 ok.</p>""",

"""        <p>If 2g ok.</p>""",

"""        <p>If 2g not ok.</p>""",

"""        <p>If 2g2 ok.</p>""",

"""        <p>If 2h ok.</p>""",

"""    <p>If 2h2 not ok.</p>""",

"""        <p>If 2i2 ok.</p>""",

"""        <p>If 2i not ok.</p>""",

"""        <p>If 2j ok.</p>""",

"""        <p>If 2j2 ok.</p>""",

"""        <p>If 2j2 not ok.</p>""",

"""        <p>If 3b ok.</p>""",

"""        <p>If 3b2 ok.</p>""",

"""        <p>If 5b ok.</p>""",

"""        <p>If 6b ok.</p>""",

"""        <p>If 6b not ok.</p>""",

"""        <p>If 7b2 ok.</p>""",

"""           <p>If 7b3 ok.</p>""",

"""<p>If 8b ok.</p>""",

"""        Never happen""",

"""    Never happen 2""",

"""            Never happen 3""",

"""                Never happen 4""",

"""x1 else""",

"""x2 else""",

"""        Never happen B""",

"""            We have company!  B""",

"""                Never happen 4 B""",

"""                And the rest of text... Bs""",

"""            Never happen 3 B""",

"""                                Never happen 4 B""",

"""                            Never happen 3 B""",

"""                We have company! B2s""",

"""                    Never happen 4 B2s""",

"""                    And the rest of text... B2s""",

"""                Never happen 3 B2s""",

"""a1- else""",

"""a-2""",

"""a3- else""",

"""a4- else""",

"""a5- else""",

"""a6- else""",

"""a-7""",

"""a8- else""",

"""        <p>@@@ If 1b2 ok.</p>""",

"""        <p>@@@ If 1b3 ok.</p>""",

"""        <p>@@@ If 2 not ok.</p>""",

"""    <p>@@@ If 2 not not ok.</p>""",

"""        <p>@@@ If 2k2 ok.</p>""",

"""        <p>@@@ If 2k not ok.</p>""",

"""        <p>@@@ If 2l not ok.</p>""",

"""        <p>@@@ If 2l2 ok.</p>""",

"""        <p>@@@ If 2l2 not ok.</p>""",

"""        <p>@@@ If 2b ok.</p>""",

"""        <p>@@@ If 2b not ok.</p>""",

"""    <p>@@@ If 2c ok.</p>""",

"""        <p>@@@ If 2c not ok.</p>""",

"""        <p>@@@ If 2d ok.</p>""",

"""        <p>If 2d not ok.</p>""",

"""        <p>@@@ If 2d2 not ok.</p>""",

"""        <p>@@@ If 2e ok.</p>""",

"""        <p>@@@ If 2e not ok.</p>""",

"""    <p>@@@ If 2f ok.</p>""",

"""        <p>@@@ If 2f2 ok.</p>""",

"""        <p>@@@ If 2g ok.</p>""",

"""        <p>@@@ If 2g not ok.</p>""",

"""        <p>@@@ If 2g2 ok.</p>""",

"""        <p>@@@ If 2h ok.</p>""",

"""    <p>@@@ If 2h2 not ok.</p>""",

"""        <p>@@@ If 2i2 ok.</p>""",

"""        <p>@@@ If 2i not ok.</p>""",

"""        <p>@@@ If 2j ok.</p>""",

"""        <p>@@@ If 2j2 ok.</p>""",

"""        <p>@@@ If 2j2 not ok.</p>""",

"""        <p>@@@ If 3b ok.</p>""",

"""        <p>@@@ If 3b2 ok.</p>""",

"""        <p>@@@ If 5b ok.</p>""",

"""        <p>@@@ If 6b ok.</p>""",

"""        <p>@@@ If 6b not ok.</p>""",

"""        <p>@@@ If 7b2 ok.</p>""",

"""           <p>@@@ If 7b3 ok.</p>""",

"""<p>@@@ If 8b ok.</p>""",

"""        @@@ Never happen""",

"""    @@@ Never happen 2""",

"""            @@@ Never happen 3""",

"""                @@@ Never happen 4""",

"""@@@ x1 else""",

"""@@@ x2 else""",

"""        @@@ Never happen B""",

"""            @@@ We have company!  B""",

"""                @@@ Never happen 4 B""",

"""                @@@ And the rest of text... Bs""",

"""            @@@ Never happen 3 B""",

"""                                @@@ Never happen 4 B""",

"""                            @@@ Never happen 3 B""",

"""                @@@ We have company! B2s""",

"""                    @@@ Never happen 4 B2s""",

"""                    @@@ And the rest of text... B2s""",

"""                @@@ Never happen 3 B2s""",

"""@@@ a1- else""",

"""@@@ a-2""",

"""@@@ a3- else""",

"""@@@ a4- else""",

"""@@@ a5- else""",

"""@@@ a6- else""",

"""@@@ a-7""",

"""@@@ a8- else""",

    tags.ifOpen, tags.ifClose, tags.foreachOpen, tags.foreachClose, tags.open, tags.close, tags.endIf, tags.endFor, tags.tabPlaceholder

    )

}