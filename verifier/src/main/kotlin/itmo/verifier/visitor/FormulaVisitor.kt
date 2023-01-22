package itmo.verifier.visitor

import CTLFormula
import itmo.verifier.model.Model
import itmo.verifier.model.State

class FormulaVisitor(val formula: CTLFormula, val kripke: Model) {

    val eval: MutableMap<State, MutableMap<CTLFormula, Boolean>>

    init {
        eval = mutableMapOf()
    }

    fun makeEval(state: State, formula: CTLFormula, status: Boolean) {
        if (eval.containsKey(state)) {
            eval[state]!![formula] = status
        } else {
            eval[state] = mutableMapOf()
            eval[state]!![formula] = status
        }
    }

    fun getEval(state: State, formula: CTLFormula): Boolean {
        return eval.getOrDefault(state, mutableMapOf()).getOrDefault(formula, false)
    }

    fun isVisited(formula: CTLFormula):Boolean {
        return eval.entries.any{it.value.containsKey(formula)}
    }

}