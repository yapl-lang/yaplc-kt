package yapl.parser.ast

data class AstForLoopExpression(
		val variable: AstName,
		val range: AstExpression,
		val action: AstExpression
) : AstExpression()
