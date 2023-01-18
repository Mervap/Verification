package itmo.verifier

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import kotlin.test.Test

class FormulaParserTest {

    @Test
    fun `simple formula test`() {
        val formulaString = "!EF !phi"

        println(CTLGrammar.parseToEnd(formulaString))
    }
}
