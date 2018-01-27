package yapl.parser.ast

import yapl.common.Bound
import yapl.parser.token.Token

abstract class AstNode {
	open val typeName get() = this::class.simpleName?.removePrefix("Ast")

	lateinit var tokens: List<Token>
	lateinit var bound: Bound
}
