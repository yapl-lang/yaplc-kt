package yapl.evaluator

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

	fun putReceiver(value: Value) {
		setLocalVariable("this", value)
	}
}
