package yapl.parser.token

import yapl.common.Bound

data class TokenOptionalKeyword(override val bound: Bound, val value: String) : Token()
