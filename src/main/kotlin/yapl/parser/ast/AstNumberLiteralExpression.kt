package yapl.parser.ast

data class AstNumberLiteralExpression(
		val beforeComma: String,
        val afterComma: String? = null,
        val afterE: String? = null
) : AstExpression()
