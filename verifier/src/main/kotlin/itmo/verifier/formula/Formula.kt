import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser

sealed class CTLFormula

object TRUE: CTLFormula()
object FALSE: CTLFormula()

data class Element(val name: String): CTLFormula()

data class Not(
    val formula: CTLFormula
): CTLFormula()

data class And(
    val left: CTLFormula,
    val right: CTLFormula
): CTLFormula()

data class Or(
    val left: CTLFormula,
    val right: CTLFormula
): CTLFormula()

data class Impl(
    val left: CTLFormula,
    val right: CTLFormula
): CTLFormula()

data class Eq(
    val left: CTLFormula,
    val right: CTLFormula
): CTLFormula()

data class AX(
    var formula: CTLFormula
): CTLFormula()

data class EX(
    var formula: CTLFormula
): CTLFormula()

data class AF(
    var formula: CTLFormula
): CTLFormula()

data class EF(
    var formula: CTLFormula
): CTLFormula()

data class AG(
    var formula: CTLFormula
): CTLFormula()

data class EG(
    var formula: CTLFormula
): CTLFormula()

data class AU(
    val left: CTLFormula,
    val right: CTLFormula
): CTLFormula()

data class EU(
    val left: CTLFormula,
    val right: CTLFormula
): CTLFormula()






object CTLGrammar: Grammar<CTLFormula>() {
    val tr by literalToken("1")
    val fal by literalToken("0")
    val lpar by literalToken("(")
    val rpar by literalToken(")")
    val lspar by literalToken("[")
    val rspar by literalToken("]")
    val not by literalToken("!")
    val and by literalToken("&")
    val or by literalToken("|")
    val impl by literalToken("->")
    val eq by literalToken("==")
    val ax by literalToken("AX")
    val ex by literalToken("EX")
    val af by literalToken("AF")
    val ef by literalToken("EF")
    val ag by literalToken("AG")
    val eg by literalToken("EG")
    val a by literalToken("A")
    val e by literalToken("E")
    val u by literalToken("U")
    val name by regexToken("\\w+")
    val ws by regexToken("\\s+", ignore = true)

    val negation by -not * parser(this::ctlExpr) map { Not(it) }
    val bracedExpression by -lpar * parser(this::implChain) * -rpar


    val axOp by -ax * parser(this::ctlExpr) map { f -> AX(f)}
    val exOp by -ex * parser(this::ctlExpr) map { f -> EX(f)}
    val afOp by -af * parser(this::ctlExpr) map { f -> AF(f)}
    val efOp by -ef * parser(this::ctlExpr) map { f -> EF(f)}
    val agOp by -ag * parser(this::ctlExpr) map { f -> AG(f)}
    val egOp by -eg * parser(this::ctlExpr) map { f -> EG(f)}
    val auOp by -a * -lspar * parser(this::ctlExpr) * -u * parser(this::ctlExpr) * -rspar map {(a, b) -> AU(a, b)}
    val euOp by -e * -lspar * parser(this::ctlExpr) * -u * parser(this::ctlExpr) * -rspar map {(a, b) -> EU(a, b)}



    val ctlExpr: Parser<CTLFormula> by
            (tr asJust TRUE) or
            (fal asJust FALSE) or
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

    val eqChain by leftAssociative(ctlExpr, eq) { a, _, b -> Eq(a, b) }
    val andChain by leftAssociative(eqChain, and) { a, _, b -> And(a, b) }
    val orChain by leftAssociative(andChain, or) { a, _, b -> Or(a, b) }
    val implChain by rightAssociative(orChain, impl) { a, _, b -> Impl(a, b) }


    override val rootParser by implChain
}

fun main(args: Array<String>) {
//    val expr = "EX [1 U phi]"
    val expr = "!EF !phi"
    println(CTLGrammar.parseToEnd(expr))
}