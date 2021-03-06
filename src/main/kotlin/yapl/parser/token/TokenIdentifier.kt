package yapl.parser.token

import yapl.common.Bound

data class TokenIdentifier(override val bound: Bound, val value: String) : Token()
