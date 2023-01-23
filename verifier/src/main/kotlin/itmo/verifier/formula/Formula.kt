import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import itmo.verifier.model.Variable
import itmo.verifier.visitor.FormulaVisitor

sealed class CTLFormula {
    abstract fun optimize(): CTLFormula
    abstract fun visit(visitor: FormulaVisitor)
    open fun compute(elements: Map<String, Variable>): Boolean {
        return true
    }
}

object TRUE : CTLFormula() {
    override fun optimize(): CTLFormula {
        return this
    }

    override fun visit(visitor: FormulaVisitor) {
        if (!visitor.isVisited(this)) {
            for (s in visitor.kripke.states.values) {
                visitor.makeEval(s, this, true)
            }
        }
        return
    }

    override fun compute(elements: Map<String, Variable>): Boolean {
        return true
    }
}

data class Element(val name: String) : CTLFormula() {
    override fun optimize(): CTLFormula {
        return this
    }

    override fun visit(visitor: FormulaVisitor) {
        if (!visitor.isVisited(this)) {
            for (s in visitor.kripke.states.values) {
                TODO("the value of the variable can be changed during recursion")
                TODO("Element is not always a Variable - it can be an Event, Action, State")
                TODO("How to handle volatile variable?")
                visitor.makeEval(s, this, visitor.kripke.variables[name]!!.value)
            }
        }
        return
    }

    override fun compute(elements: Map<String, Variable>): Boolean {
        return elements[name]!!.value
    }
}

data class Not(
    val formula: CTLFormula
) : CTLFormula() {
    override fun optimize(): CTLFormula {
        if (formula is Not) {
            return formula.formula.optimize()
        }
        return Not(formula.optimize())
    }

    override fun visit(visitor: FormulaVisitor) {
        if (!visitor.isVisited(this)) {
            formula.visit(visitor)
            for (s in visitor.kripke.states.values) {
                visitor.makeEval(s, this, !(visitor.getEval(s, formula)))
            }
        }
        return
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

    override fun visit(visitor: FormulaVisitor) {
        if (!visitor.isVisited(this)) {
            left.visit(visitor)
            right.visit(visitor)
            for (s in visitor.kripke.states.values) {
                visitor.makeEval(s, this, visitor.getEval(s, left) || visitor.getEval(s, right))
            }
        }
    }

    override fun compute(elements: Map<String, Variable>): Boolean {
        return left.compute(elements) || right.compute(elements)
    }
}

data class EX(
    val formula: CTLFormula
) : CTLFormula() {
    override fun optimize(): CTLFormula {
        return EX(formula.optimize())
    }
    override fun visit(visitor: FormulaVisitor) {
        if (!visitor.isVisited(this)) {
            formula.visit(visitor)
            for (s in visitor.kripke.states.values) {
                visitor.makeEval(s, this, false)
                val ts = s.outgoingTransitions
                val transitions = visitor.kripke.transitions
                for (t in ts) {
                    TODO("Cannot just go without making calls, changing variables")
                    if (visitor.getEval(visitor.kripke.states[transitions[t]!!.to]!!, formula)) {
                        visitor.makeEval(s, this, true)
                    }
                }
            }
        }
    }
}

data class AU(
    val left: CTLFormula,
    val right: CTLFormula
) : CTLFormula() {
    override fun optimize(): CTLFormula {
        return AU(left.optimize(), right.optimize())
    }

    override fun visit(visitor: FormulaVisitor) {
        TODO("Not yet implemented")
    }
}

data class EU(
    val left: CTLFormula,
    val right: CTLFormula
) : CTLFormula() {
    override fun optimize(): CTLFormula {
        return EU(left.optimize(), right.optimize())
    }

    override fun visit(visitor: FormulaVisitor) {
        TODO("Not yet implemented")
    }
}


object CTLGrammar : Grammar<CTLFormula>() {
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
    val ax by regexToken("AX\\b")
    val ex by regexToken("EX\\b")
    val af by regexToken("AF\\b")
    val ef by regexToken("EF\\b")
    val ag by regexToken("AG\\b")
    val eg by regexToken("EG\\b")
    val au by regexToken("AU\\b")
    val eu by regexToken("EU\\b")
    val name by regexToken("\\w+")
    val ws by regexToken("\\s+", ignore = true)

    val negation by -not * parser(this::ctlExpr) map { Not(it) }
    val bracedExpression by -lpar * parser(this::implChain) * -rpar


    val axOp by -ax * parser(this::bracedExpression) map { f -> Not(EX(Not(f))) }
    val exOp by -ex * parser(this::bracedExpression) map { f -> EX(f) }
    val afOp by -af * parser(this::bracedExpression) map { f -> AU(TRUE, f) }
    val efOp by -ef * parser(this::bracedExpression) map { f -> EU(TRUE, f) }
    val agOp by -ag * parser(this::bracedExpression) map { f -> Not(EU(TRUE, Not(f))) }
    val egOp by -eg * parser(this::bracedExpression) map { f -> Not(AU(TRUE, Not(f))) }
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