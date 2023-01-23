package itmo.analyzer.ast

import com.github.javaparser.JavaParser
import com.github.javaparser.ParseResult
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ParserConfiguration.LanguageLevel.JAVA_17
import com.github.javaparser.ast.CompilationUnit
import java.io.File

fun File.parseJavaCode(): CompilationUnit = tryParseJavaCode().getOrThrow()
fun String.parseJavaCode(): CompilationUnit = tryParseJavaCode().getOrThrow()

fun File.tryParseJavaCode(): ParseResult<CompilationUnit> = parser.parse(this)
fun String.tryParseJavaCode(): ParseResult<CompilationUnit> = parser.parse(this)

private fun <T> ParseResult<T>.getOrThrow(): T = result.orElseThrow {
    RuntimeException("""
        |Cannot parse Java code: 
        |  ${problems.joinToString("\n|  ") { it.verboseMessage }}
    """.trimMargin())
}

private val parser = JavaParser(ParserConfiguration().setLanguageLevel(JAVA_17))