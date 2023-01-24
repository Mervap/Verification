package itmo.verifier

import CTLFormula
import itmo.verifier.model.Model
import itmo.verifier.model.State
import itmo.verifier.visitor.FormulaVisitor
import java.util.BitSet

class Checker(val model: Model, val formula: CTLFormula) {

    fun way(visitor: FormulaVisitor, curr: State, elements: BitSet): MutableList<String>? {
        if (visitor.getEval(curr, elements, visitor.formula)) {
            return null
        }
        val outTrs = curr.outgoingTransitions
        if (outTrs.isEmpty()) {
            return mutableListOf()
        }
        val currElements = BitSet()
        currElements.or(elements)

        val variableOrder = visitor.kripke.variableOrder

        for (t in outTrs) {
            val transition = model.transitions[t]!!
            transition.code.forEach { (variable, value) ->
                currElements[variableOrder[variable.name]!!] = value
            }
            transition.actions.forEach { action ->
                currElements[variableOrder[action.name]!!] = true
            }
            transition.events.forEach { event ->
                currElements[variableOrder[event.name]!!] = true
            }
            val res = way(visitor, model.states[transition.to]!!, currElements) ?: continue
            res.add(t)
            return res
        }
        return mutableListOf()
    }

    fun check(start: String):List<String> {
        val startElements = BitSet()
        startElements.or(model.variableValues)

        val visitor = FormulaVisitor(formula, model)
        formula.visit(visitor)
        val state = visitor.eval[model.states[start]]
        if (state!![startElements]!![formula] == true) {
            return listOf()
        } else {
            var currState = model.states[start]!!
            var currElements = BitSet()
            currElements.or(startElements)

            val way: List<String> = way(visitor, currState, currElements)!!
            return way
            TODO("return way which is not appropriate for formula")
        }
    }
}
