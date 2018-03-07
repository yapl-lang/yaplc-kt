package yapl.evaluator

class ValueClass(val env: Environment, type: TypeClass) : Value(type) {
	val members = mutableMapOf<String, Value>()

	private fun bindMethod(name: String): Value? {
		var clazz: TypeClass? = type as TypeClass
		while (clazz != null) {
			clazz.memberFunctions.firstOrNull {
				it.function.name?.value == name || it.function.operator?.value?.contains(name) ?: false
			}?.let {
				return ValueBindFunction(this, it)
			}

			clazz = clazz.parent
		}
		return null
	}

	fun getMember(name: String) = members[name] ?: bindMethod(name)
			?: throw RuntimeException("Member $name does not exist on ${toString()}")

	fun setMember(name: String, value: Value) {
		members[name] = value
	}

	override fun toString() = "Class ${type!!.name}"
}
