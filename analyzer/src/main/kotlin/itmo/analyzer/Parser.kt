package itmo.analyzer

import com.github.javaparser.JavaParser
import com.github.javaparser.ParseResult
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ParserConfiguration.LanguageLevel.JAVA_17
import com.github.javaparser.ast.CompilationUnit
import java.io.File
import java.lang.RuntimeException

fun File.parseJavaCode(): CompilationUnit = parser.parse(this).getOrThrow()

fun String.parseJavaCode(): CompilationUnit = parser.parse(this).getOrThrow()

private fun <T> ParseResult<T>.getOrThrow(): T = result.orElseThrow {
    RuntimeException("""
        |Cannot parse Java code: 
          |${problems.joinToString("\n|")}
    """.trimMargin())
}

private val parser = JavaParser(ParserConfiguration().setLanguageLevel(JAVA_17))