package itmo.analyzer.analyze

import com.github.javaparser.Position
import com.github.javaparser.ast.visitor.VoidVisitor
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import itmo.analyzer.Project

/**
 * Entrypoint for analyzers.
 *
 * Heirs can register founded problems using [ProblemFinderContext].
 */
typealias ProblemFinder = VoidVisitor<ProblemFinderContext>
typealias ProblemFinderAdapter = VoidVisitorAdapter<ProblemFinderContext>

data class Problem(val position: Position, val message: String) {
    val presentation: String
        get() = "$position: $message"
}

/**
 * Helper for problem collection. Provide some additional information like [Project]
 */
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

