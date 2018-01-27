package yapl.evaluator

data class ValueArray(val value: Array<Value>) : Value(TypeArray) {
	override fun toString() = value.joinToString()
}
