package yapl.parser.token

import yapl.common.Bound

data class TokenStringLiteral(override val bound: Bound, val value: String) : Token()
