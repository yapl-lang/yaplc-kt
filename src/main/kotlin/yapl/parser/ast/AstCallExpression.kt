package yapl.parser.ast

data class AstCallExpression(
		val callee: AstExpression,
		val arguments: AstNodeList<AstArgument> = listOf(),
		val lambda: AstFunction? = null
) : AstExpression()
