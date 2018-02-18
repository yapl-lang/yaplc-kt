package yapl.evaluator

import yapl.parser.ast.AstArgument
import yapl.parser.ast.AstCallExpression
import yapl.parser.ast.AstName
import yapl.parser.ast.AstReferenceExpression

class ValueDynamicMemberReference(
		val evaluator: Evaluator,
		val env: Environment,
		val receiver: Value,
		val member: Value) : ValueReference() {
	override var value: Value
		get() = when (receiver) {
			is ValueString -> ValueString(receiver.value[(member as ValueNumber).value.toInt()].toString())
			is ValueArray  -> receiver.value[(member as ValueNumber).value.toInt()]
			is ValueClass  -> env.extend().run {
				setLocalVariable("b", member)
				val operatorMethod = receiver.getMember("[]")
				val callee = AstCallExpression(AstReferenceExpression(AstName("")), listOf(
//					    AstArgument(null, AstReferenceExpression(AstName("a"))),
						AstArgument(null, AstReferenceExpression(AstName("b")))
				))
				with(evaluator) { call(callee, operatorMethod, receiver) }
			}
			else           -> TODO()
		}
		set(value) {
			when (receiver) {
				is ValueArray -> receiver.value[(member as ValueNumber).value.toInt()] = value
				else          -> TODO()
			}
		}
}
