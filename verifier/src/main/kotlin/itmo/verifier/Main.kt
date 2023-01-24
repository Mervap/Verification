package itmo.verifier

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import itmo.verifier.formula.CTLGrammar
import itmo.verifier.model.Model
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import java.io.File
import java.io.PrintStream
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    if (2 > args.size || args.size > 3) {
        println("Usage: Main <file with model> <file with ctl formula> [<output file>]")
        exitProcess(0)
    }
    val modelFile = args[0]
    val formulaFile = args[1]
    val outputFile = if (args.size < 3) System.out else PrintStream(File(args[2]).outputStream())

    val modelText = File(modelFile).readText()
    val formulaText = File(formulaFile).readText()

    val model = Model(getDiagram(modelText))
    val ctlFormula = CTLGrammar.parseToEnd(formulaText).optimize()

    val checker = Checker(model, ctlFormula)
    outputFile.println(checker.check()[0])


}

fun getDiagram(modelText: String): Diagram {
    val module = SerializersModule {}
    val xml = XML(module) {
        indentString = "    "
        xmlDeclMode = XmlDeclMode.Minimal
        autoPolymorphic = true
    }

    val serializer = serializer<Diagram>()
    return xml.decodeFromString(serializer, modelText)
}