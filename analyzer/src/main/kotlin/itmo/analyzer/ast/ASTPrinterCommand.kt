package itmo.analyzer.ast

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.arguments.argument
import java.io.File

class ASTPrinterCommand : CliktCommand(name = "printAST", help = "Prints AST for given java file") {

    private val filePath by argument()

    override fun run() {
        val file = File(filePath)
        val absolutePath = file.absoluteFile.canonicalPath
        val parseResult by lazy { file.tryParseJavaCode() }
        when {
            !file.exists() -> {
                println("File '$absolutePath' not exists")
            }
            file.isDirectory -> {
                println("'$absolutePath' is a directory")
            }
            !parseResult.isSuccessful -> {
                println("""
                    |Cannot parse '$absolutePath' as java file
                    |  ${parseResult.problems.joinToString("\n  |") { it.verboseMessage }}
                """.trimMargin())
            }
            else -> {
                println(parseResult.result.get().printAST())
            }
        }

    }
}