package itmo.analyzer.analyze

import com.github.javaparser.ast.CompilationUnit
import kotlin.io.path.relativeTo
import kotlin.jvm.optionals.getOrNull

class WrongPackageFinder : ProblemFinderAdapter() {

    override fun visit(n: CompilationUnit, arg: ProblemFinderContext) {
        val storage = n.storage.getOrNull() ?: return
        val relativePath = storage.path.relativeTo(arg.project.projectDir)
        val packageName = n.packageDeclaration.getOrNull()?.name
        if (packageName == null) {
            arg.addProblem(n.begin.get(), "No package for file '$relativePath'")
            return
        }

        val actualPackage = packageName.asString()
        try {
            val expectedLocation = storage.sourceRoot.resolve(actualPackage.replace(".", "/"))
            if (expectedLocation != storage.directory) {
                throw RuntimeException("Bad location")
            }
        }
        catch (e : RuntimeException) {
            arg.addProblem(packageName.begin.get(), "Unexpected package for '$relativePath': $actualPackage")
        }
    }
}