package yapl.parser.token

import yapl.common.Bound

data class TokenKeyword(override val bound: Bound, val value: String) : Token()
