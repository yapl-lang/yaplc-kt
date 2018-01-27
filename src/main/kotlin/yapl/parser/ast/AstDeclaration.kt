package yapl.parser.ast

abstract class AstDeclaration : AstExpression() {
	abstract val modifiers: AstDeclarationModifiers
	abstract val name: AstName?
}
