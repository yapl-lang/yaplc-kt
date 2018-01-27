package yapl.evaluator

data class ValueString(val value: String) : Value(TypeString) {
	override fun toString() = value
}
