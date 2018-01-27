package yapl.common

interface Reader {
	val source: Source

	fun position(): Position

	fun peek(): Char
	fun next(): Char
	fun eof(): Boolean = peek() == Char.NULL
	fun eol(): Boolean = "\r\n".contains(peek(), true)
}

fun Reader.take(char: Char) = if (peek() == char) {
	next()
	true
} else false

inline fun Reader.takeWhile(cond: (char: Char) -> Boolean) =
		takeWhile { char, _ -> cond(char) }

inline fun Reader.takeWhile(cond: (char: Char, i: Int) -> Boolean) = buildString {
	while (!eof() && cond(peek(), length)) {
		append(next())
	}
}

fun Reader.readMaybeEscaped(): Char {
	val char = next()
//	if (char == '\\') {
//		val underEscape = next()
//		return when (underEscape) {
//			'0' -> 0.toChar()
//			'n' -> '\n'
//			else -> underEscape
//		}
//	}
	return char
}
