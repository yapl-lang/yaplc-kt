package yapl.parser.token

import yapl.common.Bound

data class TokenNumberLiteral(
		override val bound: Bound,
		val beforeComma: String,
		val afterComma: String? = null,
		val afterE: String? = null
) : Token()
