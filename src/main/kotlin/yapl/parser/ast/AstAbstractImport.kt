package yapl.parser.ast

abstract class AstAbstractImport : AstNode() {
	abstract fun computeImports(): Map<String, String?>
}
