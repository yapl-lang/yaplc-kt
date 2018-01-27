package yapl.parser.ast

data class AstArgument(
		val name: AstName? = null,
		val value: AstExpression
) : AstNode()
