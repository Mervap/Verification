package itmo.analyzer.ast

import com.github.javaparser.ast.CompilationUnit
import java.nio.file.Path
import kotlin.jvm.optionals.getOrNull

val CompilationUnit.fileName: String?
    get() = storage.getOrNull()?.fileName

val CompilationUnit.path: Path?
    get() = storage.getOrNull()?.path