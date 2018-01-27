package yapl.parser.token

import yapl.common.Bound

data class TokenError(override val bound: Bound, val message: String) : Token() {
	override val isWhitespace get() = true
}
