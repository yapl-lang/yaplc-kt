package yapl.evaluator

import java.math.BigDecimal

data class ValueNumber(val value: BigDecimal) : Value(TypeNumber) {
	constructor(value: Int) : this(BigDecimal(value))

	override fun toString() = value.toString()
}
