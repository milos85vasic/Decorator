package net.milosvasic.decorator.separator

import net.milosvasic.tautology.separator.Separator

object Separator {

    abstract class MemberSeparator(value: String) : Separator(value)

    class MEMBER : MemberSeparator(".")

}