package yapl.evaluator

class ValueVarReference(val env: Environment, val name: String) : ValueReference() {
	override var value: Value
		get() = env.getLocalVariable(name) ?: throw RuntimeException("Variable does not exist")
		set(value) = env.setLocalVariable(name, value)
}
