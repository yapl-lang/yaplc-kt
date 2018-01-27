package yapl.parser.ast

data class AstIfExpression(
		val condition: AstExpression,
		val thenBlock: AstExpression,
		val elseBlock: AstExpression? = null
) : AstExpression()
