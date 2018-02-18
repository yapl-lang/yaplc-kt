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
				val rangeEnv = import("yapl.range.Range")!!
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
				val rangeEnv = import("yapl.range.RangeClosed")!!
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
			fun stringRepresent(value: Value) = when (value) {
				is ValueString -> value.value
				is ValueNumber -> value.value.toPlainString().replace("\\.0$".toRegex(), "")
				is ValueBoolean -> if (value.value) "true" else "false"
				is ValueArray -> "array"
				is ValueFunction -> "function"
				is ValueNull -> "null"
				is ValueVoid -> "void"
				is ValueNothing -> "nothing"
				is ValueClass -> {
					try {
						(with(evaluator) {
							call(AstCallExpression(AstReferenceExpression(AstName(""))),
									value.getMember("toString"))
						} as ValueString).value
					} catch (_: Throwable) {
						"class ${value.type?.name}"
					}
				}
				else -> TODO()
			}

			ValueString(stringRepresent(a) + stringRepresent(b))
		}
	}

	fun Environment.evaluate(operator: BinaryOperator, a: Value, b: Value): Value? {
		val aClass = a::class
		val bClass = b::class

		@Suppress("UNCHECKED_CAST")
		val entry = operatorMap.filter { it.operator == operator }
				.firstOrNull { it.a::class.isInstance(aClass) && it.b::class.isInstance(bClass) }
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
