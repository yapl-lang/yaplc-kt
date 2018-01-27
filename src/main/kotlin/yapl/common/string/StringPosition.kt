package yapl.common.string

import yapl.common.Position
import yapl.common.Source

data class StringPosition(
	override val source: Source?,
	val offset: Int,
	override val line: Int,
	override val column: Int) : Position {

	override fun toString() = toString(false)

	override fun toString(source: Boolean) = buildString {
		append(line)
		append(':')
		append(column)

		if (source && this@StringPosition.source != null) {
			append(" (")
			append(this@StringPosition.source)
			append(')')
		}
	}
}
