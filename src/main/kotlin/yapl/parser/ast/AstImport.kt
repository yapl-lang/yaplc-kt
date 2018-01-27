package yapl.parser.ast

data class AstImport(val value: AstDotDelimitedName, val alias: AstName? = null) : AstAbstractImport() {
	override fun computeImports() = mapOf(value.value to alias?.value)
}
