package yapl.parser.ast

data class AstProgram(
    val `package`: AstPackage?,
    val imports: AstNodeList<AstAbstractImport>,
    val declarations: AstNodeList<AstDeclaration>
) : AstNode()
