package yapl.evaluator

import java.math.BigDecimal

data class ValueNumber(val value: BigDecimal) : Value(TypeNumber) {
	override fun toString() = value.toString()
}
