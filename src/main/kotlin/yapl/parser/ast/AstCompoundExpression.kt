package yapl.parser.ast

data class AstCompoundExpression(
		val expressions: AstNodeList<AstExpression>
) : AstExpression()
