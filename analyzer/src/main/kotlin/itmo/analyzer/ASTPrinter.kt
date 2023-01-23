package itmo.analyzer

import com.github.javaparser.Position
import com.github.javaparser.ast.*
import com.github.javaparser.ast.body.*
import com.github.javaparser.ast.comments.BlockComment
import com.github.javaparser.ast.comments.JavadocComment
import com.github.javaparser.ast.comments.LineComment
import com.github.javaparser.ast.expr.*
import com.github.javaparser.ast.modules.*
import com.github.javaparser.ast.stmt.*
import com.github.javaparser.ast.type.*
import com.github.javaparser.ast.visitor.VoidVisitorAdapter
import com.github.javaparser.printer.Printer
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration
import com.github.javaparser.printer.configuration.PrinterConfiguration
import java.util.*
import kotlin.jvm.optionals.getOrNull

fun CompilationUnit.printAST(): String = ASTPrinter().print(this)

class ASTPrinter : Printer {
    private var configuration: PrinterConfiguration = DefaultPrinterConfiguration()

    override fun print(node: Node): String = buildString {
        node.accept(ASTPrinterVisitor(this, configuration), "")
    }

    override fun setConfiguration(configuration: PrinterConfiguration): Printer {
        this.configuration = configuration
        return this
    }

    override fun getConfiguration(): PrinterConfiguration = configuration
}


/**
 * Cannot use [VoidVisitorAdapter] - it do it in wierd order
 */
private class ASTPrinterVisitor(val out: StringBuilder, val configuration: PrinterConfiguration) : VoidVisitorAdapter<String>() {
    override fun visit(n: AnnotationDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("ANNOTATION_DECLARATION", arg)
        n.annotations.acceptAll(childIndent)
        n.modifiers.acceptAll(childIndent)
        n.name.accept(childIndent)
        n.members.acceptAll(childIndent)
    }

    override fun visit(n: AnnotationMemberDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("ANNOTATION_MEMBER_DECLARATION", arg)
        n.annotations.acceptAll(childIndent)
        n.modifiers.acceptAll(childIndent)
        n.type.accept(childIndent)
        n.name.accept(childIndent)
        n.defaultValue.acceptIfPresent(childIndent)
    }

    override fun visit(n: ArrayAccessExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("ARRAY_ACCESS_EXPRESSION", arg)
        n.name.accept(childIndent)
        n.index.accept(childIndent)
    }

    override fun visit(n: ArrayCreationExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("ARRAY_CREATION_EXPRESSION", arg)
        n.elementType.accept(childIndent)
        n.levels.acceptAll(childIndent)
        n.initializer.acceptIfPresent(childIndent)
    }

    override fun visit(n: ArrayInitializerExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("ARRAY_INITIALIZER_EXPR", arg)
        n.values.acceptAll(childIndent)
    }

    override fun visit(n: AssertStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("ASSERT_STATEMENT", arg)
        n.check.accept(childIndent)
        n.message.acceptIfPresent(childIndent)
    }

    override fun visit(n: AssignExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("ASSIGN_EXPRESSION", arg)
        n.target.accept(childIndent)
        n.value.accept(childIndent)
    }

    override fun visit(n: BinaryExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("BINARY_EXPRESSION", arg, additionalInfo = n.operator.asString())
        n.left.accept(childIndent)
        n.right.accept(childIndent)
    }

    override fun visit(n: BlockComment, arg: String) {
        n.appendNode("BLOCK_COMMENT", arg)
    }

    override fun visit(n: BlockStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("BLOCK_STATEMENT", arg)
        n.statements.acceptAll(childIndent)
    }

    override fun visit(n: BooleanLiteralExpr, arg: String) {
        n.appendNode("BOOLEAN_LITERAL_EXPRESSION", arg)
    }

    override fun visit(n: BreakStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("BREAK_STATEMENT", arg)
        n.label.acceptIfPresent(childIndent)
    }

    override fun visit(n: CastExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("CAST_EXPRESSION", arg)
        n.type.accept(childIndent)
        n.expression.accept(childIndent)
    }

    override fun visit(n: CatchClause, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("CATCH_CLAUSE", arg)
        n.parameter.accept(childIndent)
        n.body.accept(childIndent)
    }

    override fun visit(n: CharLiteralExpr, arg: String) {
        n.appendNode("CHAR_LITERAL_EXPRESSIOn", arg)
    }

    override fun visit(n: ClassExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("CLASS_EXPRESSION", arg)
        n.type.accept(childIndent)
    }

    override fun visit(n: ClassOrInterfaceDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("CLASS_OR_INTERFACE_DECLARATION", arg)
        n.annotations.acceptAll(childIndent)
        n.modifiers.acceptAll(childIndent)
        n.name.accept(childIndent)
        n.typeParameters.acceptAll(childIndent)
        n.extendedTypes.acceptAll(childIndent)
        n.implementedTypes.acceptAll(childIndent)
        n.members.acceptAll(childIndent)
    }

    override fun visit(n: ClassOrInterfaceType, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("CLASS_OR_INTERFACE_TYPE", arg)
        n.annotations.acceptAll(childIndent)
        n.scope.acceptIfPresent(childIndent)
        n.name.accept(childIndent)
        n.typeArguments.acceptAll(childIndent)
    }

    override fun visit(n: CompilationUnit, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("JAVA_FILE", arg, additionalInfo = n.storage.getOrNull()?.fileName)
        n.module.acceptIfPresent(childIndent)
        n.packageDeclaration.acceptIfPresent(childIndent)
        n.imports.acceptAll(childIndent)
        n.types.acceptAll(childIndent)
    }

    override fun visit(n: ConditionalExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("CONDITIONAL_EXPRESSION", arg)
        n.condition.accept(childIndent)
        n.thenExpr.accept(childIndent)
        n.elseExpr.accept(childIndent)
    }

    override fun visit(n: ConstructorDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("CONSTRUCTOR_DECLARATION", arg)
        n.annotations.acceptAll(childIndent)
        n.modifiers.acceptAll(childIndent)
        n.typeParameters.acceptAll(childIndent)
        n.name.accept(childIndent)
        n.receiverParameter.acceptIfPresent(childIndent)
        n.parameters.acceptAll(childIndent)
        n.thrownExceptions.acceptAll(childIndent)
        n.body.accept(childIndent)
    }

    override fun visit(n: ContinueStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("CONTINUE_STATEMENT", arg)
        n.label.acceptIfPresent(childIndent)
    }

    override fun visit(n: DoStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("DO_STATEMENT", arg)
        n.condition.accept(childIndent)
        n.body.accept(childIndent)
    }

    override fun visit(n: DoubleLiteralExpr, arg: String) {
        n.appendNode("DOUBLE_LITERAL_EXPRESSION", arg)
    }

    override fun visit(n: EmptyStmt, arg: String) {
        n.appendNode("EMPTY_STATEMENT", arg)
    }

    override fun visit(n: EnclosedExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("ENCLOSED_EXPRESSION", arg)
        n.inner.accept(childIndent)
    }

    override fun visit(n: EnumConstantDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("ENUM_CONSTANT_DECLARATION", arg)
        n.annotations.acceptAll(childIndent)
        n.name.accept(childIndent)
        n.arguments.acceptAll(childIndent)
        n.classBody.acceptAll(childIndent)
    }

    override fun visit(n: EnumDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("ENUM_DECLARATION", arg)
        n.annotations.acceptAll(childIndent)
        n.modifiers.acceptAll(childIndent)
        n.name.accept(childIndent)
        n.implementedTypes.acceptAll(childIndent)
        n.entries.acceptAll(childIndent)
        n.members.acceptAll(childIndent)
    }

    override fun visit(n: ExplicitConstructorInvocationStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("EXPLICIT_CONSTRUCTOR_INVOCATION_STATEMENT", arg)
        n.expression.acceptIfPresent(childIndent)
        n.typeArguments.acceptAll(childIndent)
        n.arguments.acceptAll(childIndent)
    }

    override fun visit(n: ExpressionStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("EXPRESSION_STATEMENT", arg)
        n.expression.accept(childIndent)
    }

    override fun visit(n: FieldAccessExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("FIELD_ACCESS_EXPRESSION", arg)
        n.scope.accept(childIndent)
        n.typeArguments.acceptAll(childIndent)
        n.name.accept(childIndent)
    }

    override fun visit(n: FieldDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("FIELD_DECLARATION", arg)
        n.annotations.acceptAll(childIndent)
        n.modifiers.acceptAll(childIndent)
        n.variables.acceptAll(childIndent)
    }

    override fun visit(n: ForEachStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("FOR_EACH_STATEMENT", arg)
        n.variable.accept(childIndent)
        n.iterable.accept(childIndent)
        n.body.accept(childIndent)
    }

    override fun visit(n: ForStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("FOR_STATEMENT", arg)
        n.initialization.acceptAll(childIndent)
        n.compare.acceptIfPresent(childIndent)
        n.update.acceptAll(childIndent)
        n.body.accept(childIndent)
    }

    override fun visit(n: IfStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("IF_STATEMENT", arg)
        n.condition.accept(childIndent)
        n.thenStmt.accept(childIndent)
        n.elseStmt.acceptIfPresent(childIndent)
    }

    override fun visit(n: InitializerDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("INITIALIZER_DECLARATION", arg)
        n.annotations.acceptAll(childIndent)
        n.body.accept(childIndent)
    }

    override fun visit(n: InstanceOfExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("INSTANCE_OF_EXPRESSION", arg)
        n.expression.accept(childIndent)
        val pattern = n.pattern.getOrNull()
        if (pattern != null) {
            pattern.accept(childIndent)
        }
        else {
            n.type.accept(childIndent)
        }
    }

    override fun visit(n: IntegerLiteralExpr, arg: String) {
        n.appendNode("INTEGER_LITERAL_EXPR", arg)
    }

    override fun visit(n: JavadocComment, arg: String) {
        n.appendNode("JAVADOC_COMMENT", arg)
    }

    override fun visit(n: LabeledStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("LABELED_STATEMENT", arg)
        n.label.accept(childIndent)
        n.statement.accept(childIndent)
    }

    override fun visit(n: LineComment, arg: String) {
        n.appendNode("LINE_COMMENT", arg)
    }

    override fun visit(n: LongLiteralExpr, arg: String) {
        n.appendNode("LONG_LITERAL_EXPRESSION", arg)
    }

    override fun visit(n: MarkerAnnotationExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("MARKER_ANNOTATION_EXPRESSION", arg)
        n.name.accept(childIndent)
    }

    override fun visit(n: MemberValuePair, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("MEMBER_VALUE_PAIR", arg)
        n.name.accept(childIndent)
        n.value.accept(childIndent)
    }

    override fun visit(n: MethodCallExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("METHOD_CALL_EXPRESSION", arg)
        n.scope.acceptIfPresent(childIndent)
        n.typeArguments.acceptAll(childIndent)
        n.name.accept(childIndent)
        n.arguments.acceptAll(childIndent)
    }

    override fun visit(n: MethodDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("METHOD_DECLARATION", arg)
        n.annotations.acceptAll(childIndent)
        n.modifiers.acceptAll(childIndent)
        n.typeParameters.acceptAll(childIndent)
        n.type.accept(childIndent)
        n.name.accept(childIndent)
        n.receiverParameter.acceptIfPresent(childIndent)
        n.parameters.acceptAll(childIndent)
        n.thrownExceptions.acceptAll(childIndent)
        n.body.acceptIfPresent(childIndent)
    }

    override fun visit(n: NameExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("NAME_EXPRESSION", arg)
        n.name.accept(childIndent)
    }

    override fun visit(n: NormalAnnotationExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("NORMAL_ANNOTATION_EXPRESSION", arg)
        n.name.accept(childIndent)
        n.pairs.acceptAll(childIndent)
    }

    override fun visit(n: NullLiteralExpr, arg: String) {
        n.appendNode("NULL_LITERAL_EXPRESSION", arg)
    }

    override fun visit(n: ObjectCreationExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("OBJECT_CREATION_EXPRESSION", arg)
        n.scope.acceptIfPresent(childIndent)
        n.type.accept(childIndent)
        n.typeArguments.acceptAll(childIndent)
        n.arguments.acceptAll(childIndent)
        n.anonymousClassBody.acceptAll(childIndent)
    }

    override fun visit(n: PackageDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("PACKAGE_DECLARATION", arg)
        n.annotations.acceptAll(childIndent)
        n.name.accept(childIndent)
    }

    override fun visit(n: Parameter, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("PARAMETER", arg)
        n.annotations.acceptAll(childIndent)
        n.type.accept(childIndent)
        n.name.accept(childIndent)
        n.varArgsAnnotations.acceptAll(childIndent)
    }

    override fun visit(n: PrimitiveType, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("PRIMITIVE_TYPE", arg)
        n.annotations.acceptAll(childIndent)
    }

    override fun visit(n: Name, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("NAME", arg, additionalInfo = n.asString())
        n.qualifier.acceptIfPresent(childIndent)
    }

    override fun visit(n: SimpleName, arg: String) {
        n.appendNode("SIMPLE_NAME", arg, additionalInfo = n.asString())
    }

    override fun visit(n: ArrayType, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("ARRAY_TYPE", arg)
        n.annotations.acceptAll(childIndent)
        n.componentType.accept(childIndent)
    }

    override fun visit(n: ArrayCreationLevel, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("ARRAY_CREATION_LEVEL", arg)
        n.annotations.acceptAll(childIndent)
        n.dimension.acceptIfPresent(childIndent)
    }

    override fun visit(n: IntersectionType, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("INTERSECTION_TYPE", arg)
        n.annotations.acceptAll(childIndent)
        n.elements.acceptAll(childIndent)
    }

    override fun visit(n: UnionType, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("UNION_TYPE", arg)
        n.annotations.acceptAll(childIndent)
        n.elements.acceptAll(childIndent)
    }

    override fun visit(n: ReturnStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("RETURN_STATEMENT", arg)
        n.expression.acceptIfPresent(childIndent)
    }

    override fun visit(n: SingleMemberAnnotationExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("SINGLE_MEMBER_ANNOTATION_EXPRESSION", arg)
        n.name.accept(childIndent)
        n.memberValue.accept(childIndent)
    }

    override fun visit(n: StringLiteralExpr, arg: String) {
        n.appendNode("STRING_LITERAL_EXPRESSION", arg)
    }

    override fun visit(n: SuperExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("SUPER_EXPRESSION", arg)
        n.typeName.acceptIfPresent(childIndent)
    }

    override fun visit(n: SwitchEntry, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("SWITCH_ENTRY", arg)
        n.labels.acceptAll(childIndent)
        n.statements.acceptAll(childIndent)
    }

    override fun visit(n: SwitchStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("SWITCH_STATEMENT", arg)
        n.selector.accept(childIndent)
        n.entries.acceptAll(childIndent)
    }

    override fun visit(n: SynchronizedStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("SYNCHRONIZED_STATEMENT", arg)
        n.expression.accept(childIndent)
        n.body.accept(childIndent)
    }

    override fun visit(n: ThisExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("THIS_EXPRESSION", arg)
        n.typeName.acceptIfPresent(childIndent)
    }

    override fun visit(n: ThrowStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("THROW_STATEMENT", arg)
        n.expression.accept(childIndent)
    }

    override fun visit(n: TryStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("TRY_STATEMENT", arg)
        n.resources.acceptAll(childIndent)
        n.tryBlock.accept(childIndent)
        n.catchClauses.acceptAll(childIndent)
        n.finallyBlock.acceptIfPresent(childIndent)
    }

    override fun visit(n: LocalClassDeclarationStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("LOCAL_CLASS_DECLARATION_STATEMENT", arg)
        n.classDeclaration.accept(childIndent)
    }

    override fun visit(n: LocalRecordDeclarationStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("LOCAL_RECORD_DECLARATION_STATEMENT", arg)
        n.recordDeclaration.accept(childIndent)
    }

    override fun visit(n: TypeParameter, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("TYPE_PARAMETER", arg)
        n.annotations.acceptAll(childIndent)
        n.name.accept(childIndent)
        n.typeBound.acceptAll(childIndent)
    }

    override fun visit(n: UnaryExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("UNARY_EXPRESSION", arg)
        n.expression.accept(childIndent)
    }

    override fun visit(n: UnknownType, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("UNKNOWN_TYPE", arg)
        n.annotations.acceptAll(childIndent)
    }

    override fun visit(n: VariableDeclarationExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("VARIABLE_DECLARATION_EXPRESSION", arg)
        n.annotations.acceptAll(childIndent)
        n.modifiers.acceptAll(childIndent)
        n.variables.acceptAll(childIndent)
    }

    override fun visit(n: VariableDeclarator, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("VARIABLE_DECLARATOR", arg)
        n.type.accept(childIndent)
        n.name.accept(childIndent)
        n.initializer.acceptIfPresent(childIndent)
    }

    override fun visit(n: VoidType, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("VOID_TYPE", arg)
        n.annotations.acceptAll(childIndent)
    }

    override fun visit(n: WhileStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("WHILE_STATEMENT", arg)
        n.body.accept(childIndent)
        n.condition.accept(childIndent)
    }

    override fun visit(n: WildcardType, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("WILDCARD_TYPE", arg)
        n.annotations.acceptAll(childIndent)
        n.extendedType.acceptIfPresent(childIndent)
        n.superType.acceptIfPresent(childIndent)
    }

    override fun visit(n: LambdaExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("LAMBDA_EXPRESSION", arg)
        n.parameters.acceptAll(childIndent)
        n.body.accept(childIndent)
    }

    override fun visit(n: MethodReferenceExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("METHOD_REFERENCE_EXPRESSION", arg, additionalInfo = n.identifier)
        n.scope.accept(childIndent)
        n.typeArguments.acceptAll(childIndent)
    }

    override fun visit(n: TypeExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("TYPE_EXPRESSION", arg)
        n.type.accept(childIndent)
    }

    override fun visit(n: ImportDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("IMPORT_DECLARATION", arg)
        n.name.accept(childIndent)
    }

    override fun visit(n: ModuleDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("MODULE_DECLARATION", arg, additionalInfo = n.name.asString())
        n.annotations.acceptAll(childIndent)
        n.name.accept(childIndent)
        n.directives.acceptAll(childIndent)
    }

    override fun visit(n: ModuleRequiresDirective, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("MODULE_REQUIRES_DIRECTIVE", arg)
        n.modifiers.acceptAll(childIndent)
        n.name.accept(childIndent)
    }

    override fun visit(n: ModuleExportsDirective, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("MODULE_EXPORTS_DIRECTIVE", arg)
        n.name.accept(childIndent)
        n.moduleNames.acceptAll(childIndent)
    }

    override fun visit(n: ModuleProvidesDirective, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("MODULE_PROVIDES_DIRECTIVE", arg)
        n.name.accept(childIndent)
        n.with.acceptAll(childIndent)
    }

    override fun visit(n: ModuleUsesDirective, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("MODULE_USES_DIRECTIVE", arg)
        n.name.accept(childIndent)
    }

    override fun visit(n: ModuleOpensDirective, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("MODULE_OPENS_DIRECTIVE", arg)
        n.name.accept(childIndent)
        n.moduleNames.acceptAll(childIndent)
    }

    override fun visit(n: UnparsableStmt, arg: String) {
        n.appendNode("UNPARSABLE_STATEMENT", arg)
    }

    override fun visit(n: ReceiverParameter, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("RECEIVER_PARAMETER", arg)
        n.annotations.acceptAll(childIndent)
        n.type.accept(childIndent)
        n.name.accept(childIndent)
    }

    override fun visit(n: VarType, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("VAR_TYPE", arg)
        n.annotations.acceptAll(childIndent)
    }

    override fun visit(n: Modifier, arg: String) {
        n.appendNode("MODIFIER", arg, additionalInfo = n.keyword.asString())
    }

    override fun visit(n: SwitchExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("SWITCH_EXPRESSION", arg)
        n.entries.acceptAll(childIndent)
        n.selector.accept(childIndent)
    }

    override fun visit(n: TextBlockLiteralExpr, arg: String) {
        n.appendNode("TEXT_BLOCK_LITERAL_EXPRESSION", arg)
    }

    override fun visit(n: YieldStmt, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("YIELD_STATEMENT", arg)
        n.expression.accept(childIndent)
    }

    override fun visit(n: PatternExpr, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("PATTERN_EXPRESSION", arg)
        n.modifiers.acceptAll(childIndent)
        n.type.accept(childIndent)
        n.name.accept(childIndent)
    }

    override fun visit(n: RecordDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("RECORD_DECLARATION", arg)
        n.annotations.acceptAll(childIndent)
        n.modifiers.acceptAll(childIndent)
        n.name.accept(childIndent)
        n.typeParameters.acceptAll(childIndent)
        n.receiverParameter.acceptIfPresent(childIndent)
        n.parameters.acceptAll(childIndent)
        n.implementedTypes.acceptAll(childIndent)
        n.members.acceptAll(childIndent)
    }

    override fun visit(n: CompactConstructorDeclaration, arg: String) = blockIndent(arg) { childIndent ->
        n.appendNode("COMPACT_CONSTRUCTOR_DECLARATION", arg)
        n.annotations.acceptAll(childIndent)
        n.modifiers.acceptAll(childIndent)
        n.typeParameters.acceptAll(childIndent)
        n.name.accept(childIndent)
        n.thrownExceptions.acceptAll(childIndent)
        n.body.accept(childIndent)
    }

    private fun NodeList<*>.acceptAll(indent: String) = forEach {
        it.accept(this@ASTPrinterVisitor, indent)
    }

    private fun Node.accept(indent: String) {
        accept(this@ASTPrinterVisitor, indent)
    }

    private fun Optional<out Node>.acceptIfPresent(indent: String) = ifPresent {
        it.accept(this@ASTPrinterVisitor, indent)
    }

    private fun Optional<out NodeList<out Node>>.acceptAll(indent: String) = ifPresent {
        it.acceptAll(indent)
    }

    private inline fun blockIndent(indent: String, body: (String) -> Unit) {
        body(indent + INDENT)
    }

    private fun Node.appendNode(name: String, indent: String, additionalInfo: String? = null) {
        comment.acceptIfPresent(indent)
        out.append(indent).append(name)
        if (additionalInfo != null) {
            out.append(" ($additionalInfo)")
        }
        range.getOrNull()?.let { range ->
            out.append(" [${range.begin.presentation()}, ${range.end.presentation()}]")
        }
        out.appendLine()
    }

    companion object {
        private const val INDENT = "  "
        private fun Position.presentation(): String = "$line:$column"
    }
}