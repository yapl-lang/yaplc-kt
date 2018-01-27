package yapl.parser.operator

class OperatorSet<out T : Operator>(val all: Map<String, T>) {
	val normal = all.filterKeys { !it.isNamed() }
	val named = all.filterKeys { it.isNamed() }

	private fun String.isNamed() = all { it.isLetter() }
}
