package itmo.verifier.visitor

import itmo.verifier.formula.CTLFormula
import itmo.verifier.model.Model
import itmo.verifier.model.State

class FormulaVisitor(val formula: CTLFormula, val kripke: Model) {

    val eval: MutableMap<State, MutableMap<CTLFormula, Boolean>> = mutableMapOf()

    fun makeEval(state: State, formula: CTLFormula, status: Boolean) {
        eval.getOrPut(state) { mutableMapOf() }[formula] = status
    }

    fun getEval(state: State, formula: CTLFormula): Boolean {
        return eval[state]?.get(formula) ?: false
    }

    fun isVisited(formula: CTLFormula): Boolean {
        return eval.values.any { formula in it }
    }
}
