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
		defineOperator(BinaryOperator.Or) { a: ValueBoolean, b: ValueBoolean ->
			ValueBoolean(a.value || b.value)
		}
		defineOperator(BinaryOperator.And) { a: ValueBoolean, b: ValueBoolean ->
			ValueBoolean(a.value && b.value)
		}

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
		defineOperator(BinaryOperator.Module) { a: ValueNumber, b: ValueNumber ->
			ValueNumber(a.value % b.value)
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

		defineOperator(BinaryOperator.Equal) { a: ValueString, b: ValueString -> ValueBoolean(a.value == b.value) }
		defineOperator(BinaryOperator.NotEqual) { a: ValueString, b: ValueString -> ValueBoolean(a.value != b.value) }

		defineOperator(BinaryOperator.Equal) { _: ValueNull, _: ValueNull -> ValueBoolean(true) }
		defineOperator(BinaryOperator.NotEqual) { _: ValueNull, _: ValueNull -> ValueBoolean(false) }

		defineOperator(BinaryOperator.Equal) { _: Value, _: ValueNull -> ValueBoolean(false) }
		defineOperator(BinaryOperator.Equal) { _: ValueNull, _: Value -> ValueBoolean(false) }
		defineOperator(BinaryOperator.NotEqual) { _: Value, _: ValueNull -> ValueBoolean(true) }
		defineOperator(BinaryOperator.NotEqual) { _: ValueNull, _: Value -> ValueBoolean(true) }

		defineOperator(BinaryOperator.Is) { a: Value, b: Type ->
			if (b is TypeClass) {
				var clazz: TypeClass? = b
				while (clazz != null) {
					if (a.type == clazz) break

					clazz = clazz.parent
				}
				ValueBoolean(clazz != null)
			} else ValueBoolean(a.type == b)
		}

		defineOperator(BinaryOperator.Range) { a: ValueNumber, b: ValueNumber ->
			with(evaluator) {
				val rangeEnv = import("spela.range.Range")!!
				val range = rangeEnv.getVariable("Range")!! as TypeClass
				val env = extend()
				env.setLocalVariable("begin", a)
				env.setLocalVariable("end", b)
				env.instantiate(AstCallExpression(AstReferenceExpression(AstName("")),
						listOf(
								AstArgument(null, AstReferenceExpression(AstName("begin"))),
								AstArgument(null, AstReferenceExpression(AstName("end")))
						)), range)
			}
		}

		defineOperator(BinaryOperator.RangeClosed) { a: ValueNumber, b: ValueNumber ->
			with(evaluator) {
				val rangeEnv = import("spela.range.RangeClosed")!!
				val range = rangeEnv.getVariable("RangeClosed")!! as TypeClass
				val env = extend()
				env.setLocalVariable("begin", a)
				env.setLocalVariable("end", b)
				env.instantiate(AstCallExpression(AstReferenceExpression(AstName("")),
						listOf(
								AstArgument(null, AstReferenceExpression(AstName("begin"))),
								AstArgument(null, AstReferenceExpression(AstName("end")))
						)), range)
			}
		}

		defineOperator(BinaryOperator.Concat) { a: Value, b: Value ->
			with(evaluator) { ValueString(stringRepresent(a) + stringRepresent(b)) }
		}
	}

	fun Environment.evaluate(operator: BinaryOperator, a: Value, b: Value): Value? {
		@Suppress("UNCHECKED_CAST")
		val entry = operatorMap.filter { it.operator == operator }
				.firstOrNull { it.a.isInstance(a) && it.b.isInstance(b) }
				as? OperatorEntry<Value, Value, Value>

		return entry?.action?.invoke(this, a, b) ?: if (a is ValueClass) extend().run {
			setLocalVariable("b", b)
			val operatorMethod = a.getMember(operator.name)
			val callee = AstCallExpression(AstReferenceExpression(AstName("")), listOf(
//					AstArgument(null, AstReferenceExpression(AstName("a"))),
					AstArgument(null, AstReferenceExpression(AstName("b")))
			))
			with(evaluator) { call(callee, operatorMethod, a) }
		} else null
	}
}
