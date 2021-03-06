package yapl.parser.operator

enum class PrefixOperator(override val precedence: Int, override vararg val value: String) : Operator {
	Not(16, "not"),
	Increment(1, "++", "inc"),
	Decrement(1, "--", "dec"),

	Typeof(32, "typeof")

	;

	override val type: Operator.Type = Operator.Type.Prefix
}

val prefixOperators = PrefixOperator.values().operators()
