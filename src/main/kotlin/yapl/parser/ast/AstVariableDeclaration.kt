package yapl.parser.ast

data class AstVariableDeclaration(
		override val modifiers: AstDeclarationModifiers,
		override val name: AstName,
		val type: AstTypeReference? = null,
		val initializer: AstExpression? = null,
		val isFinal: Boolean = true
) : AstDeclaration()
