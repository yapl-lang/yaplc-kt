package yapl.evaluator

import yapl.parser.ast.AstCallExpression
import yapl.parser.ast.AstName
import yapl.parser.ast.AstReferenceExpression

class Environment(val parent: Environment? = null) {
	private val ownVars = mutableMapOf<String, Value>()

	fun extend() = Environment(this)


	fun createVariable(name: String, value: Value): ValueReference? {
		if (ownVars.containsKey(name)) {
			return null
		}
		ownVars[name] = value
		return ValueVarReference(this, name)
	}

	fun getVariable(name: String): Value? = ownVars.getOrElse(name) { parent?.getVariable(name) }

	fun getVariableReference(name: String): ValueReference? =
			if (ownVars.containsKey(name))
				ValueVarReference(this, name)
			else
				parent?.getVariableReference(name)


	fun getLocalVariable(name: String) = ownVars[name]
	fun setLocalVariable(name: String, value: Value) {
		ownVars[name] = value
	}

	fun setVariable(name: String, value: Value): Boolean = if (ownVars.containsKey(name)) {
		ownVars[name] = value
		true
	} else parent?.setVariable(name, value) ?: false

	fun putReceiver(receiver: Value) {
		setLocalVariable("this", receiver)

		if (receiver is ValueClass) receiver.members.forEach { key, _ ->
			setLocalVariable(key, ValueMemberReference(receiver, key))
		}
	}

	fun Evaluator.hashCode(value: Value): Int = when (value) {
		is ValueString -> value.value.hashCode()
		is ValueNumber -> value.value.hashCode()
		is ValueBoolean -> if (value.value) 1 else 0
		is ValueArray -> value.value.hashCode()
		is ValueFunction -> value.function.hashCode()
		is ValueNull -> 0
		is ValueVoid -> 0
		is ValueNothing -> 0
		is ValueClass -> {
			try {
				hashCode(call(AstCallExpression(AstReferenceExpression(AstName(""))),
						value.getMember("hashCode")))
			} catch (_: Throwable) {
				value.hashCode()
			}
		}
		is Type -> value.hashCode()
		else -> TODO()
	}

	fun Evaluator.stringRepresent(value: Value): String = when (value) {
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
				stringRepresent(call(AstCallExpression(AstReferenceExpression(AstName(""))),
						value.getMember("toString")))
			} catch (_: Throwable) {
				"object ${value.type?.name}"
			}
		}
		is TypeClass -> buildString {
			append("class ")
			append(value.declaration.name?.value ?: "anonymous")

			value.declaration.parent?.let {
				append(" extends ")
				if (it.callee is AstReferenceExpression) append(it.callee.name.value)
			}
		}
		is Type -> value.name
		else -> TODO()
	}
}
