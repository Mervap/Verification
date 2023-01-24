package itmo.verifier

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import itmo.verifier.model.Model
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.jupiter.api.Test

class CheckerTest {
    @Test
    fun `serialization sample test`() {
        val formulaString = "EX(PRESTART)"
        val module = SerializersModule {}
        val xml = XML(module) {
            indentString = "    "
            xmlDeclMode = XmlDeclMode.Minimal
            autoPolymorphic = true
        }

        val serializer = serializer<Diagram>()
        val m = Model(xml.decodeFromString(serializer, SerializationTest.XML_STRING))
        var formula = CTLGrammar.parseToEnd(formulaString)
        val checker = Checker(m, formula)
        val res = checker.check("Start")
        println(res)
    }
}