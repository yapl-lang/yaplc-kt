package yapl.common.string

import yapl.common.NULL
import yapl.common.Reader
import yapl.common.Source
import yapl.common.isNewline

class StringReader(override val source: Source, private val string: String) : Reader {
	constructor(source: StringSource) : this(source, source.text)

	private var offset: Int = 0
	private var line: Int = 1
	private var column: Int = 0

	private var cachedPosition: StringPosition? = null

	override fun position() = cachedPosition ?:
		StringPosition(source, offset, line, column).also { cachedPosition = it }

	override fun peek() = if (offset == string.length) Char.NULL else string[offset]

	override fun next(): Char {
		if (eof()) {
			return peek()
		}

		val next = peek()

		cachedPosition = null
		++offset
		if (next.isNewline()) {
			column = 0
			++line
		} else {
			++column
		}

		return next
	}
}
