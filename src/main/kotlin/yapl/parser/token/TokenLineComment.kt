package yapl.parser.token

import yapl.common.Bound

data class TokenLineComment(override val bound: Bound, val value: String) : Token() {
	override val isWhitespace get() = true
}
