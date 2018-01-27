package yapl.evaluator

class ValueMemberReference(val obj: ValueClass, val name: String) : ValueReference() {
	override var value: Value
		get() = obj.getMember(name)
		set(value) {
			obj.setMember(name, value)
		}
}
