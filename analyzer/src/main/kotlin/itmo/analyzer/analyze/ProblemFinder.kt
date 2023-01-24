package itmo.analyzer.analyze

import com.github.javaparser.Position
import com.github.javaparser.ast.visitor.VoidVisitor
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import itmo.analyzer.Project

typealias ProblemFinder = VoidVisitor<ProblemFinderContext>
typealias ProblemFinderAdapter = VoidVisitorAdapter<ProblemFinderContext>

data class Problem(val position: Position, val message: String) {
    val presentation: String
        get() = "$position: $message"
}

data class ProblemFinderContext(val project: Project) {
    private val mutableProblems = mutableListOf<Problem>()
    val problems: List<Problem> get() = mutableProblems

    fun addProblem(position: Position, message: String) {
        mutableProblems.add(Problem(position, message))
    }

    fun addProblem(problem: Problem) {
        mutableProblems.add(problem)
    }
}

