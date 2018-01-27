package yapl.common

data class Bound(val begin: Position, val end: Position) {
	init {
		if (begin.source !== end.source) {
			throw RuntimeException("Bound should have the same source of begin and end")
		}
	}

	val source = begin.source

	override fun toString() = toString(false)

	fun toString(source: Boolean) = buildString {
		append(begin)
		append('-')
		append(end)

		if (source && this@Bound.source != null) {
			append(" (")
			append(this@Bound.source)
			append(')')
		}
	}
}
