package yapl.parser.ast

data class AstBinaryCall(
		val left: AstExpression,
		val right: AstExpression,
		val method: AstReferenceExpression,
		val isInverted: Boolean = false
) : AstExpression()
