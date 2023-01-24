package itmo.verifier

import itmo.verifier.formula.CTLFormula
import itmo.verifier.model.Model
import itmo.verifier.model.State
import itmo.verifier.visitor.FormulaVisitor

class Checker(val model: Model, val formula: CTLFormula) {

    fun way(visitor: FormulaVisitor, curr: State): MutableList<String>? {
        if (visitor.getEval(curr, visitor.formula)) {
            return null
        }
        val outTrs = curr.outgoingTransitions
        if (outTrs.isEmpty()) {
            return mutableListOf()
        }

        for (t in outTrs) {
            val transition = model.transitions[t]!!
            val res = way(visitor, model.states[transition.to]!!) ?: continue
            res.add(t)
            return res
        }
        return mutableListOf()
    }

    fun check(start: String):List<String> {
        val visitor = FormulaVisitor(formula, model)
        formula.visit(visitor)
        val state = visitor.eval[model.states[start]]!!
        if (state[formula] == true) {
            return listOf()
        } else {
            var currState = model.states[start]!!
            val way: List<String> = way(visitor, currState)!!
            return way
            TODO("return way which is not appropriate for formula")
        }
    }
}
