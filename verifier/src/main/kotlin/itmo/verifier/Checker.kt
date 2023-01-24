package itmo.verifier

import itmo.verifier.formula.CTLFormula
import itmo.verifier.model.Model
import itmo.verifier.model.State
import itmo.verifier.visitor.FormulaVisitor

class Checker(val model: Model, val formula: CTLFormula) {
    fun way(visitor: FormulaVisitor, curr: State): MutableList<String>? {
        TODO("build a way")
        return mutableListOf()
    }

    fun check(): List<String> {
        formula.optimize()
        val visitor = FormulaVisitor(formula, model)
        formula.visit(visitor)
        val state = visitor.eval[model.startState]!!
        return if (state[formula] == true) {
            listOf("Formula is true for model")
        } else {
    //            var currState = model.startState
    //            val way: List<String> = way(visitor, currState)!!
            listOf("Formula is false for model")
        }
    }
}
