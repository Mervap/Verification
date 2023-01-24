package itmo.verifier.model

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import itmo.verifier.*
import itmo.verifier.formula.CTLFormula
import itmo.verifier.formula.CTLGrammar

enum class VariableType(s: String) {
    VOLATILE("volatile"), PLAIN("")
}

const val DEFAULT_STATE_NAME: String = "DEFAULT"

class Model(diagram: Diagram) {
    lateinit var startState: State
    val name: String
    val autoReject: Boolean
    val events: List<EventKripke>
    val variables: Map<String, Variable>
    val variableOrder: MutableMap<String, Int>
    val states: Map<String, State>
    val transitions: Map<String, Transition>
    val addedTransitions: MutableSet<String>

    init {
        addedTransitions = mutableSetOf()
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
        variableOrder = variables.keys.asSequence().withIndex().map { it.value to it.index }.toMap().toMutableMap()

        name = diagram.name
        states = mutableMapOf()
        transitions = mutableMapOf()
        for (w in diagram.widgets) {
            if (w.type == "Transition") {
                val eventsList = mutableListOf<EventKripke>()
                val actionsList = mutableListOf<ActionKripke>()
                var codeList = listOf<Assign>()
                val guardList = mutableListOf<GuardKripke>()
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

                val from = w.id + "in"
                val to = w.id + "out"
                addedTransitions.add(from)
                addedTransitions.add(to)
                states[w.id] = State(
                    w.id,
                    w.id,
                    0,
                    mutableSetOf(from),
                    mutableSetOf(to),
                    eventsList.asSequence().map { it.name }.toSet() +
                            actionsList.asSequence().map { it.name }.toSet(),
                )

                transitions[w.id] = Transition(w.id, from, to, eventsList, codeList, actionsList, guardList)
                transitions[from] = Transition(from, "", "", eventsList, codeList, actionsList, guardList)
                transitions[to] = Transition(to, "", "", eventsList, codeList, actionsList, guardList)

                actionsList.forEach {
                    variableOrder[it.name] = variableOrder.size
                }
            } else {
                var name = DEFAULT_STATE_NAME
                var type: Int = 0
                val incoming = mutableSetOf<String>()
                val outgoing = mutableSetOf<String>()
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
                if (type == 1) {
                    startState = states[w.id]!!
                }
            }
        }
        for (s in states.values) {
            if (!transitions.containsKey(s.id)) {
                for (t in s.incomingTransitions) {
                    transitions[t]!!.to = s.id
                    transitions[t + "out"]!!.from = t
                    transitions[t + "out"]!!.to = s.id
                }
                s.incomingTransitions.addAll(s.incomingTransitions.map { it + "out" })
                for (t in s.outgoingTransitions) {
                    transitions[t]!!.from = s.id
                    transitions[t + "in"]!!.from = s.id
                    transitions[t + "in"]!!.to = t
                }
                s.outgoingTransitions.addAll(s.outgoingTransitions.map { it + "in" })

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

    fun parseDeclaration(s: String): Variable {
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

class State(
    val id: String,
    val name: String,
    val type: Int,
    val incomingTransitions: MutableSet<String>,
    val outgoingTransitions: MutableSet<String>,
    val elements: Set<String> = setOf(name),
)

class Transition(
    val id: String,
    var from: String,
    var to: String,
    val events: List<EventKripke>,
    val code: List<Assign>,
    val actions: List<ActionKripke>,
    val guard: List<GuardKripke>
)

class EventKripke(val name: String)

class GuardKripke(text: String) {

    private val formula: CTLFormula = CTLGrammar.parseToEnd(text).optimize()

    fun compute(variables: Map<String, Variable>): Boolean {
        return formula.compute(variables)
    }

}

class ActionKripke(val name: String, val synchro: Boolean)

class Variable(val name: String, val type: VariableType, var value: Boolean)

data class Assign(val t: Variable, val value: Boolean)
