package itmo.analyzer

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.core.subcommands
import itmo.analyzer.ast.ASTPrinterCommand

class AnalyzerCommand : CliktCommand() {
    override fun run() = Unit
}

fun main(args: Array<String>) = AnalyzerCommand()
    .subcommands(ASTPrinterCommand())
    .main(args)
