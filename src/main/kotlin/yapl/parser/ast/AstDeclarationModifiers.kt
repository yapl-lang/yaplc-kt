package yapl.parser.ast

data class AstDeclarationModifiers(
		val values: AstNodeList<AstDeclarationModifier> = listOf()
) : AstNode() {
	fun has(name: String) = values.any { it.value == name }
}
