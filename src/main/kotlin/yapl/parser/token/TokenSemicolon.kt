package yapl.parser.token

import yapl.common.Bound

data class TokenSemicolon(override val bound: Bound, val value: String) : Token()
