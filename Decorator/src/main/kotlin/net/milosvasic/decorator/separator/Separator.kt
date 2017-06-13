package net.milosvasic.decorator.separator

import net.milosvasic.tautology.separator.Separator

object Separator {

    abstract class MemberSeparator(value: String) : Separator(value)

    class MEMBER : MemberSeparator(".")

    class ARRAY_OPEN : MemberSeparator("[")

    class ARRAY_CLOSE : MemberSeparator("]")

}