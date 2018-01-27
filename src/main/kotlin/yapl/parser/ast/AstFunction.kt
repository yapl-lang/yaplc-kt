package yapl.parser.ast

import yapl.parser.operator.Operator

data class AstFunction(
		override val modifiers: AstDeclarationModifiers = AstDeclarationModifiers(),
		override val name: AstName? = null,
		val operator: Operator? = null,
		val parameters: AstNodeList<AstFunctionParameter> = listOf(),
		val returnTypes: AstNodeList<AstTypeReference> = listOf(),
		val body: AstExpression? = null,
		val isExpression: Boolean = false,
		val isMethod: Boolean = false,
		val isLambda: Boolean = false
) : AstDeclaration()
