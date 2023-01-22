package itmo.verifier

import AU
import CTLGrammar
import EU
import EX
import Element
import Not
import Or
import TRUE
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import kotlin.test.Test
import kotlin.test.assertEquals

class FormulaParserTest {

    @Test
    fun `true test`() {
        val formulaString = "1"

        assertEquals(CTLGrammar.parseToEnd(formulaString), TRUE)
    }

    @Test
    fun `not test`() {
        val formulaString = "!(1)"

        assertEquals(CTLGrammar.parseToEnd(formulaString), Not(TRUE))
    }

    @Test
    fun `not and test`() {
        val formulaString = "!(USB && POE)"

        assertEquals(CTLGrammar.parseToEnd(formulaString).optimize(), Or(Not(Element("USB")), Not(Element(name="POE"))))
    }

    @Test
    fun `and test`() {
        val formulaString = "1 && 0"

        assertEquals(CTLGrammar.parseToEnd(formulaString), Not(Or(Not(TRUE), Not(Not(TRUE)))))
    }

    @Test
    fun `impl test`() {
        val formulaString = "1 -> abacaba"

        assertEquals(CTLGrammar.parseToEnd(formulaString), Or(Not(TRUE), Element("abacaba")))
    }

    @Test
    fun `ex test`() {
        val formulaString = "EX(future)"

        assertEquals(CTLGrammar.parseToEnd(formulaString), EX(Element("future")))
    }

    @Test
    fun `ef test`() {
        val formulaString = "EF(future)"

        assertEquals(CTLGrammar.parseToEnd(formulaString), EU(TRUE, Element("future")))
    }

    @Test
    fun `eg test`() {
        val formulaString = "EG(future)"

        assertEquals(CTLGrammar.parseToEnd(formulaString), Not(AU(TRUE, Not(Element("future")))))
    }

    @Test
    fun `au test`() {
        val formulaString = "AU [ 1 , EX(future | 0)]"

        assertEquals(CTLGrammar.parseToEnd(formulaString), AU(TRUE, EX(Or(Element("future"), Not(TRUE)))))
    }

    @Test
    fun `spaces test`() {
        val formulaString = "\tAU           [ \t   1, EX(future \n | 0)\n]\n\n\n"

        assertEquals(CTLGrammar.parseToEnd(formulaString), AU(TRUE, EX(Or(Element("future"), Not(TRUE)))))
    }


    @Test
    fun `brackets test`() {
        val formulaString = "(1)   -> (((EX future)))\n    "

        val l = formulaString.split("(\\s)+".toRegex())
        assertEquals(CTLGrammar.parseToEnd(formulaString), Or(Not(TRUE), EX(Element("future"))))
    }


    @Test
    fun `optimization test`() {

        assertEquals(Not(Or(Not(TRUE), Not(Not(TRUE)))).optimize(), Not(Or(Not(TRUE), TRUE)))
        assertEquals(Not(Or(Not(Not(Not(TRUE))), Not(Not(TRUE)))).optimize(), Not(Or(Not(TRUE), TRUE)))
        assertEquals(Not(Not(Not(Or(Not(Not(Not(TRUE))), Not(Not(TRUE)))))).optimize(), Not(Or(Not(TRUE), TRUE)))
    }
}
