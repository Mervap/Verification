package itmo.verifier

import itmo.verifier.SerializationTest.Companion.XML_STRING
import itmo.verifier.model.Model
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import org.junit.jupiter.api.Test


class KripkeBuildTest {
    @Test
    fun `serialization sample test`() {
        val module = SerializersModule {}
        val xml = XML(module) {
            indentString = "    "
            xmlDeclMode = XmlDeclMode.Minimal
            autoPolymorphic = true
        }

        val serializer = serializer<Diagram>()
        val m = Model(xml.decodeFromString(serializer, XML_STRING))
        println(m)
    }
}
