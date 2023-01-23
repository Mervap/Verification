package itmo.verifier.visitor

import CTLFormula
import itmo.verifier.model.Model
import itmo.verifier.model.State

class FormulaVisitor(val formula: CTLFormula, val kripke: Model) {

    val eval: MutableMap<State, MutableMap<Map<String, Boolean>, MutableMap<CTLFormula, Boolean>>>

    init {
        eval = mutableMapOf()
    }

    fun makeEval(state: State, variables: Map<String, Boolean>, formula: CTLFormula, status: Boolean) {
        eval.getOrPut(state) { mutableMapOf() }
            .getOrPut(variables) { mutableMapOf() }
            .put(formula, status)
    }

    fun getEval(state: State, variables: Map<String, Boolean>, formula: CTLFormula): Boolean {
        return eval.get(state)?.get(variables)?.get(formula) ?: false
    }

    fun isVisited(variables: Map<String, Boolean>, formula: CTLFormula):Boolean {
        return eval.entries.any { it.value[variables]?.containsKey(formula) ?: false }
    }

}
