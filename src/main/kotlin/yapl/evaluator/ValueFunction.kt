package yapl.evaluator

import yapl.parser.ast.AstFunction

data class ValueFunction(val env: Environment, val function: AstFunction) : Value(TypeFunction) {
	override fun toString() = "Function"
}
