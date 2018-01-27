package yapl.evaluator

import yapl.parser.ast.AstClass
import yapl.parser.ast.AstFunction

class TypeClass(
		evaluator: Evaluator,
		env: Environment,
		val declaration: AstClass
) : Type(declaration.name?.value ?: "Anonymous class") {
	val memberFunctions = declaration.members
			.mapNotNull { it as? AstFunction }
			.map { ValueFunction(env, it) }
	val parent = declaration.parent?.let { with(evaluator) { env.evaluate(it.callee) } } as TypeClass?
}
