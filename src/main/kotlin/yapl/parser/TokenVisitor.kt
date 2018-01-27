package yapl.parser

import yapl.parser.token.*

interface TokenVisitor {
	fun TokenError.visit() = Unit
	fun TokenIdentifier.visit() = Unit
	fun TokenKeyword.visit() = Unit
	fun TokenLineComment.visit() = Unit
	fun TokenNewline.visit() = Unit
	fun TokenOptionalKeyword.visit() = Unit
	fun TokenPunctuation.visit() = Unit
	fun TokenSemicolon.visit() = Unit
	fun TokenWhitespace.visit() = Unit
}
