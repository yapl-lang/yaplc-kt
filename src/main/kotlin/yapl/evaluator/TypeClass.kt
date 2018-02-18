package yapl.evaluator

import yapl.parser.ast.AstClass
import yapl.parser.ast.AstClassConstructor
import yapl.parser.ast.AstFunction

class TypeClass(
		val env: Environment,
		val declaration: AstClass
) : Type(declaration.name?.value ?: "Anonymous class") {
	var parent: TypeClass? = null
	lateinit var memberConstructors: List<Pair<Environment, AstClassConstructor>>
	lateinit var memberFunctions: List<ValueFunction>

	fun Evaluator.init() {
		parent = unrefNullable(declaration.parent?.let { env.evaluate(it.callee) }) as TypeClass?

		memberConstructors = declaration.members
				.filterIsInstance<AstClassConstructor>()
				.map { Pair(env, it) }

		memberFunctions = declaration.members
				.filterIsInstance<AstFunction>()
				.map { ValueFunction(env, it) }
	}
}
