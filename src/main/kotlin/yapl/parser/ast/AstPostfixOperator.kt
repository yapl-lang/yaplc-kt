package yapl.parser.ast

import yapl.parser.operator.PostfixOperator

data class AstPostfixOperator(
		val operand: AstExpression,
		val operator: PostfixOperator
) : AstExpression()
