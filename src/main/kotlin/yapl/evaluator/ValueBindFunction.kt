package yapl.evaluator

data class ValueBindFunction(
		val receiver: ValueClass,
		val callee: Value
) : Value(TypeFunction) {
	override fun toString() = callee.toString()
}
