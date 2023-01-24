package itmo.verifier.visitor

import CTLFormula
import itmo.verifier.model.Model
import itmo.verifier.model.State
import java.util.BitSet

class FormulaVisitor(val formula: CTLFormula, val kripke: Model) {

    val eval: MutableMap<State, MutableMap<BitSet, MutableMap<CTLFormula, Boolean>>>

    init {
        eval = mutableMapOf()
    }

    fun makeEval(state: State, variables: BitSet, formula: CTLFormula, status: Boolean) {
        eval.getOrPut(state) { mutableMapOf() }
            .getOrPut(variables) { mutableMapOf() }
            .put(formula, status)
    }

    fun getEval(state: State, variables: BitSet, formula: CTLFormula): Boolean {
        return eval.get(state)?.get(variables)?.get(formula) ?: false
    }

    fun isVisited(variables: BitSet, formula: CTLFormula):Boolean {
        return eval.entries.any { it.value[variables]?.containsKey(formula) ?: false }
    }

}
