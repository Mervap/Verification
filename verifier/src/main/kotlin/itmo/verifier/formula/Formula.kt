import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import itmo.verifier.model.Variable

sealed class CTLFormula {
    abstract fun optimize():CTLFormula
    open fun compute(elements: Map<String, Variable>): Boolean {
        return true
    }
}

object TRUE: CTLFormula() {
    override fun optimize(): CTLFormula {
        return this
    }

    override fun compute(elements: Map<String, Variable>): Boolean {
        return true
    }
}

data class Element(val name: String): CTLFormula() {
    override fun optimize(): CTLFormula {
        return this
    }

    override fun compute(elements: Map<String, Variable>): Boolean {
        return elements[name]!!.value
    }
}

data class Not(
    val formula: CTLFormula
): CTLFormula() {
    override fun optimize(): CTLFormula {
        if (formula is Not) {
            return formula.formula.optimize()
        }
        return Not(formula.optimize())
    }

    override fun compute(elements: Map<String, Variable>): Boolean {
        return !formula.compute(elements)
    }
}

data class Or(
    val left: CTLFormula,
    val right: CTLFormula
): CTLFormula() {
    override fun optimize(): CTLFormula {
        return Or(left.optimize(), right.optimize())
    }

    override fun compute(elements: Map<String, Variable>): Boolean {
        return left.compute(elements) || right.compute(elements)
    }
}

data class EX(
    val formula: CTLFormula
): CTLFormula() {
    override fun optimize(): CTLFormula {
        return EX(formula.optimize())
    }
}

data class AU(
    val left: CTLFormula,
    val right: CTLFormula
): CTLFormula() {
    override fun optimize(): CTLFormula {
        return AU(left.optimize(), right.optimize())
    }
}

data class EU(
    val left: CTLFormula,
    val right: CTLFormula
): CTLFormula() {
    override fun optimize(): CTLFormula {
        return EU(left.optimize(), right.optimize())
    }
}


object CTLGrammar: Grammar<CTLFormula>() {
    val tr by literalToken("1")
    val fal by literalToken("0")
    val lpar by literalToken("(")
    val rpar by literalToken(")")
    val lspar by literalToken("[")
    val rspar by literalToken("]")
    val not by literalToken("!")
    val and by literalToken("&&")
    val or by literalToken("||")
    val impl by literalToken("->")
    val eq by literalToken("==")
    val comma by literalToken(",")
    val ax by literalToken("AX")
    val ex by literalToken("EX")
    val af by literalToken("AF")
    val ef by literalToken("EF")
    val ag by literalToken("AG")
    val eg by literalToken("EG")
    val au by literalToken("AU")
    val eu by literalToken("EU")
    val name by regexToken("\\w+")
    val ws by regexToken("\\s+", ignore = true)

    val negation by -not * parser(this::ctlExpr) map { Not(it) }
    val bracedExpression by -lpar * parser(this::implChain) * -rpar


    val axOp by -ax * parser(this::bracedExpression) map { f -> Not(EX(Not(f)))}
    val exOp by -ex * parser(this::bracedExpression) map { f -> EX(f)}
    val afOp by -af * parser(this::bracedExpression) map { f -> AU(TRUE, f)}
    val efOp by -ef * parser(this::bracedExpression) map { f -> EU(TRUE, f)}
    val agOp by -ag * parser(this::bracedExpression) map { f -> Not(EU(TRUE, Not(f)))}
    val egOp by -eg * parser(this::bracedExpression) map { f -> Not(AU(TRUE, Not(f)))}
    val auOp by -au * -lspar * parser(this::ctlExpr) * -comma * parser(this::ctlExpr) * -rspar map {(a, b) -> AU(a, b)}
    val euOp by -eu * -lspar * parser(this::ctlExpr) * -comma * parser(this::ctlExpr) * -rspar map { (a, b) -> EU(a, b)}



    val ctlExpr: Parser<CTLFormula> by
            (tr asJust TRUE) or
            (fal asJust Not(TRUE)) or
            (name map { Element(it.text) }) or
            negation or
            bracedExpression or
            axOp or
            exOp or
            afOp or
            efOp or
            agOp or
            egOp or
            auOp or
            euOp

    val eqChain by leftAssociative(ctlExpr, eq) { a, _, b -> Or(Not(Or(a, b)), Not(Or(Not(a), Not(b)))) }
    val andChain by leftAssociative(eqChain, and) { a, _, b -> Not(Or(Not(a), Not(b))) }
    val orChain by leftAssociative(andChain, or) { a, _, b -> Or(a, b) }
    val implChain by rightAssociative(orChain, impl) { a, _, b -> Or(Not(a), b) }


    override val rootParser by implChain
}
