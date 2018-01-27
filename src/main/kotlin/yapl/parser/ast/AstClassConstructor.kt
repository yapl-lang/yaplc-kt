package yapl.parser.ast

data class AstClassConstructor(
		override val modifiers: AstDeclarationModifiers = AstDeclarationModifiers(),
		override val name: AstName? = null,
		val parameters: AstNodeList<AstFunctionParameter> = listOf(),
		val body: AstExpression? = null
) : AstDeclaration()
