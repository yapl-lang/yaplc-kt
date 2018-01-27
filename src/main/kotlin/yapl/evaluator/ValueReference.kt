package yapl.evaluator

abstract class ValueReference : Value(TypeReference) {
	abstract var value: Value

	override fun toString() = "Reference $value"
}
