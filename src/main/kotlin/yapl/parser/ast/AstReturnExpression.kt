package yapl.parser.ast

data class AstReturnExpression(
		val results: AstNodeList<AstExpression>
) : AstExpression()
