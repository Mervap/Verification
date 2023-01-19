package itmo.verifier.model

import com.github.h0tk3y.betterParse.combinators.*
import com.github.h0tk3y.betterParse.grammar.Grammar
import com.github.h0tk3y.betterParse.grammar.parser
import com.github.h0tk3y.betterParse.lexer.literalToken
import com.github.h0tk3y.betterParse.lexer.regexToken
import com.github.h0tk3y.betterParse.parser.Parser
import itmo.verifier.Diagram

enum class VariableType {
    VOLATILE, PLAIN
}

class Model(diagram:Diagram) {
}

class State(id: String, name: String, type: Int, transitions: List<Transition>) {

}

class Transition(id: String, event: Event) {

}

class Event(name: String, code: List<Assign>, actions: List<Action>) {

}

class Action(name: String, synchro: String) {

}

class Variable(name: String, type: VariableType, value: Boolean) {

}

data class Assign(val t: Variable, val value: Boolean)
