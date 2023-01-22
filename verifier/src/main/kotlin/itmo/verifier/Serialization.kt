package itmo.verifier

import kotlinx.serialization.Serializable
import nl.adaptivity.xmlutil.serialization.XmlElement
import nl.adaptivity.xmlutil.serialization.XmlSerialName
import nl.adaptivity.xmlutil.serialization.XmlValue

@Serializable
@XmlSerialName("diagram", "", "")
data class Diagram(
    @XmlElement(true)
    val name: String,
    val data: Data,
    val widgets: List<Widget>,
)

@Serializable
@XmlSerialName("data", "", "")
data class Data(val stateMachine: StateMachine)

@Serializable
@XmlSerialName("Statemachine", "", "")
data class StateMachine(
    val events: List<Event>,

    val variables: List<Variable>,

    @XmlSerialName("autoreject", "", "")
    @XmlElement(true)
    val autoReject: Boolean = true,
)

@Serializable
@XmlSerialName("variable", "", "")
data class Variable(
    @XmlSerialName("decl", "", "")
    val declaration: String,
)

@Serializable
@XmlSerialName("widget", "", "")
data class Widget(
    val id: String,
    val type: String,
    val attributes: Attributes,
)

@Serializable
@XmlSerialName("attributes", "", "")
data class Attributes(
    val attributes: List<WidgetAttribute>,
)

@Serializable
sealed interface WidgetAttribute

@Serializable
@XmlSerialName("name", "", "")
data class Name(@XmlValue(true) val name: String = "") : WidgetAttribute

@Serializable
@XmlSerialName("type", "", "")
data class Type(@XmlValue(true) val type: Int = 0) : WidgetAttribute

@Serializable
@XmlSerialName("event", "", "")
data class Event(val name: String, val comment: String) : WidgetAttribute

@Serializable
@XmlSerialName("action", "", "")
data class Action(val name: String, val comment: String, val synchro: String) : WidgetAttribute

@Serializable
@XmlSerialName("code", "", "")
data class Code(@XmlValue(true) val code: String = "") : WidgetAttribute

@Serializable
@XmlSerialName("guard", "", "")
data class Guard(@XmlValue(true) val guard: String = "") : WidgetAttribute

@Serializable
@XmlSerialName("incoming", "", "")
data class Incoming(val id: String) : WidgetAttribute

@Serializable
@XmlSerialName("outgoing", "", "")
data class Outgoing(val id: String) : WidgetAttribute
