package yapl.parser.ast

data class AstFunctionParameter(
		override val modifiers: AstDeclarationModifiers = AstDeclarationModifiers(),
		override val name: AstName,
		val type: AstTypeReference? = null,
        val default: AstExpression? = null
) : AstDeclaration()
