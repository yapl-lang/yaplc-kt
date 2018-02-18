package yapl.parser.ast

data class AstWhileLoopExpression(
		val condition: AstExpression,
		val action: AstExpression
) : AstExpression()
