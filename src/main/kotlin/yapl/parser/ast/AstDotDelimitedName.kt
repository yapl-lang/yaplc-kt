package yapl.parser.ast

data class AstDotDelimitedName(val entries: List<String>) : AstNode() {
	val value by lazy { entries.joinToString(".") }

	override fun toString() = value
}
