package yapl.evaluator

data class ValueBoolean(val value: Boolean) : Value(TypeBoolean) {
	override fun toString() = if (value) "true" else "false"
}
