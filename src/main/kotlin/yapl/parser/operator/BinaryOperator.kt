package yapl.parser.operator

enum class BinaryOperator(override val precedence: Int, override vararg val value: String) : Operator {
	Assignment(1, "="),

	Range(2, "..."),
	RangeClosed(2, "..<"),

	Or(4, "or"),
	And(5, "and"),

	Less(7, "<"),
	More(7, ">"),
	LessEqual(7, "<="),
	MoreEqual(7, ">="),
	Equal(7, "=="),
	NotEqual(7, "!="),

	Concat(9, "~"),

	Plus(10, "+", "plus"),
	Minus(10, "-", "minus"),
	Times(20, "*", "times"),
	Divide(20, "/", "div"),
	Module(20, "%", "rem"),

	Power(30, "**", "pow"),

	Member(40, "->"),
	MemberDynamic(40, "[]"),

	Is(2, "is"),

	;

	override val type: Operator.Type = Operator.Type.Binary
}

val binaryOperators = BinaryOperator.values().operators()
