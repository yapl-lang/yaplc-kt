package yapl.evaluator

import yapl.parser.ast.AstFunction

data class ValueInternalFunction(
		val declaration: AstFunction,
		val function: (Environment) -> Value
) : Value(TypeFunction) {
	override fun toString() = "Internal Function"

}
