package itmo.verifier.model

import CTLFormula
import CTLGrammar
import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.parseToEnd
import itmo.verifier.*

enum class VariableType(s: String) {
    VOLATILE("volatile"), PLAIN("")
}

val DEFAULT_STATE_NAME: String = "DEFAULT"

class Model(diagram:Diagram) {
    val name: String
    val autoReject: Boolean
    val events: List<EventKripke>
    val variables: Map<String, Variable>
    val states: Map<String, State>
    val transitions: Map<String, Transition>

    init {
        autoReject = diagram.data.stateMachine.autoReject
        events = mutableListOf()
        for (e in diagram.data.stateMachine.events) {
            events.add(EventKripke(e.name))
        }
        variables = mutableMapOf()
        for (d in diagram.data.stateMachine.variables) {
            val v = parseDeclaration(d.declaration)
            variables[v.name] = v
        }
        name = diagram.name
        states = mutableMapOf()
        transitions = mutableMapOf()
        for (w in diagram.widgets) {
            if (w.type == "Transition") {
                val eventsList = mutableListOf<EventKripke>()
                val actionsList = mutableListOf<ActionKripke>()
                var codeList = listOf<Assign>()
                var guardList = mutableListOf<GuardKripke>()
                for (a in w.attributes.attributes) {
                    if (a is Event) {
                        eventsList.add(EventKripke(a.name))
                    } else if (a is Action) {
                        val synchroVal = a.synchro == "1"
                        actionsList.add(ActionKripke(a.name, synchroVal))
                    } else if (a is Code) {
                        codeList = parseCode(a.code, variables)
                    } else if (a is Guard) {
                        if (a.guard.isEmpty()) {
                            guardList.add(GuardKripke("1"))
                        } else {
                            guardList.add(GuardKripke(a.guard))
                        }
                    }
                }
                transitions[w.id] = Transition(w.id, eventsList, codeList, actionsList, guardList)
            } else {
                var name = DEFAULT_STATE_NAME
                var type: Int = 0
                var incoming = mutableSetOf<String>()
                var outgoing = mutableSetOf<String>()
                for (a in w.attributes.attributes) {
                    if (a is Name) {
                        name = a.name
                    } else if (a is Type) {
                        type = a.type
                    } else if (a is Incoming) {
                        incoming.add(a.id)
                    } else if (a is Outgoing) {
                        outgoing.add(a.id)
                    }
                }
                states[w.id] = State(w.id, name, type, incoming, outgoing)
            }
        }
    }



    fun parseCode(s: String, vars: Map<String, Variable>): List<Assign> {
        val lines = s.split(";(\\s)+".toRegex())
        val res = mutableListOf<Assign>()
        for (l in lines) {
            if (l.isEmpty())
                continue
            val lexList = l.split("(\\s)+".toRegex())
            val name = lexList[0]
            val value = lexList[2] == "1"
            res.add(Assign(vars.get(name)!!, value))
        }
        return res
    }

    fun parseDeclaration(s: String):Variable {
        val decl = s.replace(";", "")
        val lexList = decl.split("(\\s)+".toRegex())
        var type = VariableType.PLAIN;
        var itemIdx = 1
        if (lexList[0] == VariableType.VOLATILE.name) {
            type = VariableType.VOLATILE
            itemIdx++
        }
        val name = lexList[itemIdx]
        val value = lexList[itemIdx + 2] == "1"

        return Variable(name, type, value)
    }
}

class State(val id: String, val name: String, val type: Int, val incomingTransitions: Set<String>, val outgoingTransitions: Set<String>) {

}

class Transition(val id: String, val events: List<EventKripke>, val code: List<Assign>, val actions: List<ActionKripke>, val guard: List<GuardKripke>) {
}

class EventKripke(val name: String) {
}

class GuardKripke(val text: String) {

    private val formula: CTLFormula = CTLGrammar.parseToEnd(text).optimize()

    fun compute(variables:Map<String, Variable>): Boolean {
        return formula.compute(variables)
    }

}

class ActionKripke(val name: String, val synchro: Boolean) {

}

class Variable(val name: String, val type: VariableType, var value: Boolean) {
}

data class Assign(val t: Variable, val value: Boolean)
