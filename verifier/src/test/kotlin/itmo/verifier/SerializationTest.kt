package itmo.verifier

import itmo.verifier.model.Model
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import nl.adaptivity.xmlutil.XmlDeclMode
import nl.adaptivity.xmlutil.serialization.XML
import kotlin.test.Test

class SerializationTest {
    companion object {
        val XML_STRING = """
            <diagram>
              <name>Switcher</name>
              <data>
                <Statemachine>
                  <event name="TIMER" comment="" />
                  <event name="INTERRUPT" comment="" />
                  <event name="OFF" comment="Off button was pressed" />
                  <event name="CHG" comment="Some of input variables were changed" />
                  <event name="tick" comment="" />
                  <variable decl="bool S1 = 0;" />
                  <variable decl="bool S2 = 0;" />
                  <variable decl="bool S3 = 0;" />
                  <variable decl="bool PS = 0;" />
                  <variable decl="bool BQ = 0;" />
                  <variable decl="bool SH = 0;" />
                  <variable decl="bool INIT = 0;" />
                  <variable decl="bool TIM4 = 0;" />
                  <variable decl="bool LOW = 0;" />
                  <variable decl="volatile bool POE = 0;" />
                  <variable decl="volatile bool USB = 0;" />
                  <variable decl="volatile bool BadBat = 0;" />
                  <autoreject>False</autoreject>
                </Statemachine>
              </data>
              <widget id="0" type="State">
                <attributes>
                  <name>Start</name>
                  <type>1</type>
                  <outgoing id="7" />
                </attributes>
              </widget>
              <widget id="7" type="Transition">
                <attributes>
                  <event name="tick" comment="" />
                  <action name="hal_init" comment="" synchro="1" />
                  <action name="tim4_enable" comment="" synchro="1" />
                  <code>INIT = 1;
            TIM4 = 1;
            </code>
                  <guard></guard>
                </attributes>
              </widget>
              <widget id="1" type="State">
                <attributes>
                  <name>PRESTART</name>
                  <type>0</type>
                  <incoming id="7" />
                  <incoming id="13" />
                  <incoming id="16" />
                  <incoming id="17" />
                  <incoming id="18" />
                  <incoming id="19" />
                  <incoming id="20" />
                  <incoming id="21" />
                  <outgoing id="8" />
                </attributes>
              </widget>
              <widget id="13" type="Transition">
                <attributes>
                  <event name="OFF" comment="Off button was pressed" />
                  <code />
                  <guard></guard>
                </attributes>
              </widget>
              <widget id="16" type="Transition">
                <attributes>
                  <event name="OFF" comment="Off button was pressed" />
                  <code />
                  <guard></guard>
                </attributes>
              </widget>
              <widget id="17" type="Transition">
                <attributes>
                  <event name="CHG" comment="Some of input variables were changed" />
                  <code />
                  <guard>BadBat &amp;&amp; (!POE)</guard>
                </attributes>
              </widget>
              <widget id="18" type="Transition">
                <attributes>
                  <event name="OFF" comment="Off button was pressed" />
                  <code />
                  <guard></guard>
                </attributes>
              </widget>
              <widget id="19" type="Transition">
                <attributes>
                  <event name="OFF" comment="Off button was pressed" />
                  <code />
                  <guard></guard>
                </attributes>
              </widget>
              <widget id="20" type="Transition">
                <attributes>
                  <event name="CHG" comment="Some of input variables were changed" />
                  <code />
                  <guard>!(USB &amp;&amp; POE)</guard>
                </attributes>
              </widget>
              <widget id="21" type="Transition">
                <attributes>
                  <event name="CHG" comment="Some of input variables were changed" />
                  <code />
                  <guard>!POE &amp;&amp; BadBat</guard>
                </attributes>
              </widget>
              <widget id="8" type="Transition">
                <attributes>
                  <event name="tick" comment="" />
                  <action name="shell_deinit" comment="" synchro="1" />
                  <action name="bq_deinit" comment="" synchro="1" />
                  <action name="pin_reset_s1" comment="" synchro="1" />
                  <action name="pin_reset_s2" comment="" synchro="1" />
                  <action name="pin_reset_s3" comment="" synchro="1" />
                  <action name="delay_5000" comment="" synchro="1" />
                  <code>SH = 0;
            BQ = 0;
            S1 = 0;
            S2 = 0;
            S3 = 0;
            PS = 0;
            </code>
                  <guard></guard>
                </attributes>
              </widget>
              <widget id="2" type="State">
                <attributes>
                  <name>POWER_ON</name>
                  <type>0</type>
                  <incoming id="8" />
                  <incoming id="11" />
                  <outgoing id="9" />
                  <outgoing id="10" />
                  <outgoing id="12" />
                  <outgoing id="16" />
                </attributes>
              </widget>
              <widget id="11" type="Transition">
                <attributes>
                  <event name="INTERRUPT" comment="" />
                  <code>TIM4 = 1;
            LOW = 0;
            </code>
                  <guard></guard>
                </attributes>
              </widget>
              <widget id="9" type="Transition">
                <attributes>
                  <event name="CHG" comment="Some of input variables were changed" />
                  <code />
                  <guard>USB &amp;&amp; POE</guard>
                </attributes>
              </widget>
              <widget id="10" type="Transition">
                <attributes>
                  <event name="TIMER" comment="" />
                  <code>TIM4 = 0;
            LOW = 1;
            </code>
                  <guard></guard>
                </attributes>
              </widget>
              <widget id="12" type="Transition">
                <attributes>
                  <event name="CHG" comment="Some of input variables were changed" />
                  <code>SH = 1;
            BQ = 1;
            S2 = 1;
            S3 = 1;
            PS = 1;
            </code>
                  <guard>(!USB) &amp;&amp; POE</guard>
                </attributes>
              </widget>
              <widget id="3" type="State">
                <attributes>
                  <name>FLASH</name>
                  <type>0</type>
                  <incoming id="9" />
                  <outgoing id="19" />
                  <outgoing id="20" />
                </attributes>
              </widget>
              <widget id="4" type="State">
                <attributes>
                  <name>SLEEP</name>
                  <type>0</type>
                  <incoming id="10" />
                  <outgoing id="11" />
                </attributes>
              </widget>
              <widget id="5" type="State">
                <attributes>
                  <name>CPU_ON</name>
                  <type>0</type>
                  <incoming id="12" />
                  <incoming id="15" />
                  <outgoing id="13" />
                  <outgoing id="14" />
                  <outgoing id="21" />
                </attributes>
              </widget>
              <widget id="15" type="Transition">
                <attributes>
                  <event name="CHG" comment="Some of input variables were changed" />
                  <code />
                  <guard>POE</guard>
                </attributes>
              </widget>
              <widget id="14" type="Transition">
                <attributes>
                  <event name="CHG" comment="Some of input variables were changed" />
                  <code />
                  <guard>!POE &amp;&amp; !BadBat</guard>
                </attributes>
              </widget>
              <widget id="6" type="State">
                <attributes>
                  <name>BAT_ONLY</name>
                  <type>0</type>
                  <incoming id="14" />
                  <outgoing id="15" />
                  <outgoing id="17" />
                  <outgoing id="18" />
                </attributes>
              </widget>
            </diagram>
        """.trimIndent()
    }
    @Test
    fun `serialization sample test`() {


        val module = SerializersModule {}
        val xml = XML(module) {
            indentString = "    "
            xmlDeclMode = XmlDeclMode.Minimal
            autoPolymorphic = true
        }

        val serializer = serializer<Diagram>()
        println(xml.decodeFromString(serializer, XML_STRING))
    }
}
