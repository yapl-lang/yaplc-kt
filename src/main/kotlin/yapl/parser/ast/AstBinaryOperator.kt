package yapl.parser.ast

import yapl.parser.operator.BinaryOperator

data class AstBinaryOperator(
		val left: AstExpression,
		val right: AstExpression,
		val operator: BinaryOperator,
		val isInverted: Boolean = false
) : AstExpression()
