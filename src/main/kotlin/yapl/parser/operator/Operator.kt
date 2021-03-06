package yapl.parser.operator

interface Operator {
	enum class Type {
		Prefix,
		Binary,
		Postfix
	}

	val name: String
	val type: Type
	val value: Array<out String>
	val precedence: Int
}

fun <T : Operator> Array<T>.operators() = OperatorSet(associateBy { it.value }
		.flatMap { entry -> entry.key.map { it to entry.value } }
		.toMap())


