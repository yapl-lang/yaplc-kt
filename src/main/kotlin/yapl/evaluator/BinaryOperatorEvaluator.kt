package yapl.evaluator

import yapl.parser.operator.BinaryOperator
import yapl.parser.operator.Operator
import kotlin.reflect.KClass

class BinaryOperatorEvaluator {
	class OperatorEntry<A : Value, B : Value, out Result : Value>(
			val operator: BinaryOperator,
			val a: KClass<A>,
			val b: KClass<B>,
			val action: (A, B) -> Result
	)

	private val operatorMap = mutableListOf<OperatorEntry<*, *, *>>()

	private inline fun <reified A : Value, reified B : Value, reified Result : Value>
			defineOperator(operator: BinaryOperator, noinline action: (A, B) -> Result) {
		operatorMap.add(OperatorEntry(operator, A::class, B::class, action))
	}

	init {
		defineOperator(BinaryOperator.Plus) { a: ValueNumber, b: ValueNumber ->
			ValueNumber(a.value + b.value)
		}
		defineOperator(BinaryOperator.Minus) { a: ValueNumber, b: ValueNumber ->
			ValueNumber(a.value - b.value)
		}
		defineOperator(BinaryOperator.Times) { a: ValueNumber, b: ValueNumber ->
			ValueNumber(a.value * b.value)
		}
		defineOperator(BinaryOperator.Divide) { a: ValueNumber, b: ValueNumber ->
			ValueNumber(a.value / b.value)
		}
		defineOperator(BinaryOperator.Equal) { a: ValueNumber, b: ValueNumber ->
			ValueBoolean(a.value == b.value)
		}
		defineOperator(BinaryOperator.NotEqual) { a: ValueNumber, b: ValueNumber ->
			ValueBoolean(a.value != b.value)
		}
		defineOperator(BinaryOperator.Less) { a: ValueNumber, b: ValueNumber ->
			ValueBoolean(a.value < b.value)
		}
		defineOperator(BinaryOperator.LessEqual) { a: ValueNumber, b: ValueNumber ->
			ValueBoolean(a.value <= b.value)
		}
		defineOperator(BinaryOperator.More) { a: ValueNumber, b: ValueNumber ->
			ValueBoolean(a.value > b.value)
		}
		defineOperator(BinaryOperator.MoreEqual) { a: ValueNumber, b: ValueNumber ->
			ValueBoolean(a.value >= b.value)
		}
	}

	fun evaluate(operator: BinaryOperator, a: Value, b: Value): Value? {
		val aClass = a::class
		val bClass = b::class
		val entry = operatorMap.filter { it.operator == operator }
				.firstOrNull { it.a == aClass && it.b == bClass }
				as? OperatorEntry<Value, Value, Value>

		return entry?.action?.invoke(a, b)
	}
}
