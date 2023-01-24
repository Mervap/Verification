package itmo.analyzer.analyze

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import itmo.analyzer.Project
import itmo.analyzer.ast.path
import java.io.File
import java.nio.file.Path
import kotlin.io.path.relativeTo

class AnalyzeCommand : CliktCommand(name = "analyze", help = "Prints to stdout project problems") {

    private val projectDir by argument()

    override fun run() {
        val baseFile = File(projectDir)
        val absolutePath = baseFile.absoluteFile.canonicalPath
        when {
            !baseFile.exists() -> {
                println("Directory '$absolutePath' not exists")
                return
            }
            !baseFile.isDirectory -> {
                println("'$absolutePath' is not a directory")
                return
            }
        }

        println(collectProjectProblems(absolutePath))
    }
}

fun collectProjectProblems(projectDir: String): String = buildString {
    val project = Project.from(projectDir)
    if (project.files.isEmpty() && project.problemFiles.isEmpty()) {
        append("No java project files found. Check that '$projectDir' is correct java project directory")
        appendLine()
        return@buildString
    }

    for ((path, file) in project.problemFiles) {
        appendFileProblems(project.projectDir, path, file.problems.map { it.verboseMessage })
    }

    var found = project.problemFiles.isNotEmpty()
    for (file in project.files) {
        val context = ProblemFinderContext(project)
        for (finder in problemFinders) {
            finder.visit(file, context)
        }

        val problems = context.problems
        if (problems.isNotEmpty()) {
            appendFileProblems(project.projectDir, file.path, problems.map { it.presentation })
            found = true
        }
    }

    if (!found) {
        append("No any problems found!")
        appendLine()
    }
}

fun StringBuilder.appendFileProblems(projectPath: Path, filepath: Path?, problems: List<String>) {
    val path = filepath?.relativeTo(projectPath)?.toString() ?: "<unknown file>"
    append("""
        |$path: ${problems.size} problems founded
        |  ${problems.joinToString("\n|  ")}
        |
    """.trimMargin())
    appendLine()
}

val problemFinders: List<ProblemFinder> = listOf(
    WrongPackageFinder()
)
