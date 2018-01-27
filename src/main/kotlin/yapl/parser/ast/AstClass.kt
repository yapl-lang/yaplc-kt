package yapl.parser.ast

data class AstClass(
		override val modifiers: AstDeclarationModifiers = AstDeclarationModifiers(),
		override val name: AstName? = null,
		val parent: AstCallExpression? = null,
		val primaryConstructor: AstClassConstructor? = null,
		val members: AstNodeList<AstDeclaration> = listOf()
) : AstDeclaration()
