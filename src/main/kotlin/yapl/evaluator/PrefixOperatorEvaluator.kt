package yapl.evaluator

import yapl.parser.ast.AstCallExpression
import yapl.parser.ast.AstName
import yapl.parser.ast.AstReferenceExpression
import yapl.parser.operator.PrefixOperator
import kotlin.reflect.KClass

class PrefixOperatorEvaluator(val evaluator: Evaluator) {
	class OperatorEntry<T : Value, out Result : Value>(
			val operator: PrefixOperator,
			val type: KClass<T>,
			val action: Environment.(T) -> Result
	)

	private val operatorMap = mutableListOf<OperatorEntry<*, *>>()

	private inline fun <reified T : Value, reified Result : Value>
			defineOperator(operator: PrefixOperator, noinline action: Environment.(T) -> Result) {
		operatorMap.add(OperatorEntry(operator, T::class, action))
	}

	init {
		defineOperator(PrefixOperator.Not) { it: ValueBoolean ->
			ValueBoolean(!it.value)
		}

		defineOperator(PrefixOperator.Typeof) { it: Value ->
			it.type!!
		}
	}

	fun Environment.evaluate(operator: PrefixOperator, operand: Value): Value? {
		@Suppress("UNCHECKED_CAST")
		val entry = operatorMap.filter { it.operator == operator }
				.firstOrNull { it.type.isInstance(operand) }
				as? OperatorEntry<Value, Value>

		return entry?.action?.invoke(this, operand) ?: if (operand is ValueClass) extend().run {
			val operatorMethod = operand.getMember(operator.name)
			val callee = AstCallExpression(AstReferenceExpression(AstName("")))
			with(evaluator) { call(callee, operatorMethod, operand) }
		} else null
	}
}
