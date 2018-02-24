package yapl.parser.ast

import yapl.parser.operator.PrefixOperator

data class AstPrefixOperator(
		val operator: PrefixOperator,
		val operand: AstExpression
) : AstExpression()
