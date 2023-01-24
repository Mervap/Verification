package itmo.analyzer

import com.github.javaparser.ParseResult
import com.github.javaparser.ParserConfiguration
import com.github.javaparser.ParserConfiguration.LanguageLevel.JAVA_17
import com.github.javaparser.ast.CompilationUnit
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration
import com.github.javaparser.utils.ParserCollectionStrategy
import com.github.javaparser.utils.SourceRoot
import com.github.javaparser.utils.SourceRoot.Callback.Result
import java.io.File
import java.nio.file.Path
import kotlin.jvm.optionals.getOrNull


/**
 * Storage of information about the project. Provide info about all files and base path
 */
data class Project(
    val projectDir: Path,
    val files: List<CompilationUnit>,
    val problemFiles: List<Pair<Path, ParseResult<CompilationUnit>>>
) {

    fun getClassByName(name: String): ClassOrInterfaceDeclaration? = files.firstNotNullOfOrNull {
        it.getClassByName(name).getOrNull()
    }

    fun getClasses(): List<ClassOrInterfaceDeclaration> = files.flatMap {
        it.types.filterIsInstance<ClassOrInterfaceDeclaration>()
    }

    companion object {
        fun from(file: File) = from(file.toPath())

        fun from(path: String) = from(Path.of(path))

        fun from(path: Path): Project {
            val roots = ParserCollectionStrategy(ParserConfiguration().setLanguageLevel(JAVA_17)).collect(path).sourceRoots
            val parsed = mutableListOf<CompilationUnit>()
            val unparsed = mutableListOf<Pair<Path, ParseResult<CompilationUnit>>>()
            val callback = ProjectCallback(parsed, unparsed)
            roots.forEach { it.parse("", callback) }
            return Project(path, parsed, unparsed)
        }

        private class ProjectCallback(
            val files: MutableList<CompilationUnit>,
            val problemFiles: MutableList<Pair<Path, ParseResult<CompilationUnit>>>
        ) : SourceRoot.Callback {
            override fun process(localPath: Path, absolutePath: Path, result: ParseResult<CompilationUnit>): Result {
                if (result.isSuccessful) {
                    files.add(result.result.get())
                }
                else {
                    problemFiles.add(absolutePath to result)
                }
                return Result.DONT_SAVE
            }
        }
    }
}