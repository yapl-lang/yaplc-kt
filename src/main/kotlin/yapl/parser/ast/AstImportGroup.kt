package yapl.parser.ast

data class AstImportGroup(val value: AstDotDelimitedName, val imports: AstNodeList<AstAbstractImport>) : AstAbstractImport() {
	override fun computeImports() = mutableMapOf<String, String?>().apply {
		// TODO: Avoid imports collisions
		imports.map { it.computeImports() }.forEach { it.forEach { name, alias ->
			put(value.value + "." + name, alias)
		} }
	}
}
