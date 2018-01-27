package yapl.parser.token

import yapl.common.Bound

data class TokenCharLiteral(override val bound: Bound, val value: Char) : Token()
