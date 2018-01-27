package yapl.evaluator

import yapl.parser.ast.AstArgument
import yapl.parser.ast.AstCallExpression
import yapl.parser.ast.AstName
import yapl.parser.ast.AstReferenceExpression
import yapl.parser.operator.BinaryOperator
import kotlin.reflect.KClass

class BinaryOperatorEvaluator(val evaluator: Evaluator) {
	class OperatorEntry<A : Value, B : Value, out Result : Value>(
			val operator: BinaryOperator,
			val a: KClass<A>,
			val b: KClass<B>,
			val action: Environment.(A, B) -> Result
	)

	private val operatorMap = mutableListOf<OperatorEntry<*, *, *>>()

	private inline fun <reified A : Value, reified B : Value, reified Result : Value>
			defineOperator(operator: BinaryOperator,
			               noinline action: Environment.(A, B) -> Result) {
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

	fun Environment.evaluate(operator: BinaryOperator, a: Value, b: Value): Value? {
		if (a is ValueClass) extend().apply {
			setLocalVariable("b", b)
			val operatorMethod = a.getMember(operator.name)
			val callee = AstCallExpression(AstReferenceExpression(AstName("")), listOf(
//					AstArgument(null, AstReferenceExpression(AstName("a"))),
					AstArgument(null, AstReferenceExpression(AstName("b")))
			))
			return with(evaluator) { call(callee, operatorMethod, a) }
		}

		val aClass = a::class
		val bClass = b::class

		@Suppress("UNCHECKED_CAST")
		val entry = operatorMap.filter { it.operator == operator }
				.firstOrNull { it.a == aClass && it.b == bClass }
				as? OperatorEntry<Value, Value, Value>

		return entry?.action?.invoke(this, a, b)
	}
}
