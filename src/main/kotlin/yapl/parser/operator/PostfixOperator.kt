package yapl.parser.operator

enum class PostfixOperator(override val precedence: Int, override vararg val value: String) : Operator {
	Increment(1, "++", "inc"),
	Decrement(1, "--", "dec"),

	;

	override val type: Operator.Type = Operator.Type.Postfix
}

val postfixOperators = PostfixOperator.values().operators()
