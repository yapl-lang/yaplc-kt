package yapl.evaluator

abstract class Value(val type: Type?) {
	abstract override fun toString(): String
}
