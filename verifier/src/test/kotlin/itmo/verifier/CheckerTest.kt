package itmo.verifier

import com.github.h0tk3y.betterParse.grammar.parseToEnd
import itmo.verifier.formula.CTLGrammar
import itmo.verifier.model.Model
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class CheckerTest {
    @Test
    fun `ex simple test`() {
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
        val res = checker.check()
        assertEquals(listOf("Formula is true for model"), res)
    }

    @Test
    fun `event simple test`() {
        val formulaString = "EX(tick)"
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
        val res = checker.check()
        assertEquals(listOf("Formula is true for model"), res)
    }

    @Test
    fun `action simple test`() {
        val formulaString = "EX(hal_init)"
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
        val res = checker.check()
        assertEquals(listOf("Formula is true for model"), res)
    }

    @Test
    fun `ef test`() {
        val formulaString = "EF (POWER_ON)"
        val module = SerializersModule {}
        val xml = XML(module) {
            indentString = "    "
            xmlDeclMode = XmlDeclMode.Minimal
            autoPolymorphic = true
        }

        val serializer = serializer<Diagram>()
        val m = Model(xml.decodeFromString(serializer, SerializationTest.XML_STRING))
        var formula = CTLGrammar.parseToEnd(formulaString).optimize()
        val checker = Checker(m, formula)
        val res = checker.check()
        assertEquals(listOf("Formula is true for model"), res)
    }

    @Test
    fun `af test`() {
        val formulaString = "AF (POWER_ON)"
        val module = SerializersModule {}
        val xml = XML(module) {
            indentString = "    "
            xmlDeclMode = XmlDeclMode.Minimal
            autoPolymorphic = true
        }

        val serializer = serializer<Diagram>()
        val m = Model(xml.decodeFromString(serializer, SerializationTest.XML_STRING))
        var formula = CTLGrammar.parseToEnd(formulaString).optimize()
        val checker = Checker(m, formula)
        val res = checker.check()
        assertEquals(listOf("Formula is true for model"), res)
    }

    @Test
    fun `af event test`() {
        val formulaString = "AF (tick)"
        val module = SerializersModule {}
        val xml = XML(module) {
            indentString = "    "
            xmlDeclMode = XmlDeclMode.Minimal
            autoPolymorphic = true
        }

        val serializer = serializer<Diagram>()
        val m = Model(xml.decodeFromString(serializer, SerializationTest.XML_STRING))
        var formula = CTLGrammar.parseToEnd(formulaString).optimize()
        val checker = Checker(m, formula)
        val res = checker.check()
        assertEquals(listOf("Formula is true for model"), res)
    }

    @Test
    fun `impl test`() {
        val formulaString = "AG (PRESTART -> AU [PRESTART, POWER_ON])"
        val module = SerializersModule {}
        val xml = XML(module) {
            indentString = "    "
            xmlDeclMode = XmlDeclMode.Minimal
            autoPolymorphic = true
        }

        val serializer = serializer<Diagram>()
        val m = Model(xml.decodeFromString(serializer, SerializationTest.XML_STRING))
        var formula = CTLGrammar.parseToEnd(formulaString).optimize()
        val checker = Checker(m, formula)
        val res = checker.check()
        assertEquals(listOf("Formula is true for model"), res)
    }

    @Test
    fun `false test`() {
        val formulaString = "AG (POWER_ON)"
        val module = SerializersModule {}
        val xml = XML(module) {
            indentString = "    "
            xmlDeclMode = XmlDeclMode.Minimal
            autoPolymorphic = true
        }

        val serializer = serializer<Diagram>()
        val m = Model(xml.decodeFromString(serializer, SerializationTest.XML_STRING))
        var formula = CTLGrammar.parseToEnd(formulaString).optimize()
        val checker = Checker(m, formula)
        val res = checker.check()
        assertEquals(listOf("Formula is false for model"), res)
    }
}