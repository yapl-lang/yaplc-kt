package yapl.parser.token

import yapl.common.Bound

data class TokenPunctuation(override val bound: Bound, val value: Char) : Token()
