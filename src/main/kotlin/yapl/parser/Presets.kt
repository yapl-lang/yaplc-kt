package yapl.parser

import yapl.parser.ast.AstClass
import yapl.parser.ast.AstFunction
import yapl.parser.ast.AstFunctionParameter

object Presets {
	val OperatorChars = listOf(
			'.', ',',

			'!', '=',

			'+', '-',
			'*', '/',
			'%',
			'^',
			'&', '|',

			'<', '>',
			'?'
	)

	val Punctuations = OperatorChars + listOf(
			'(', ')',
			'[', ']',
			'{', '}',

			':', '@'
	)

	val Keywords = listOf(
			"package",
			"import",
			"fun", "class",
			"as",

			"val", "var",

			"if", "then", "else",

			"return"
	)

	val OptionalKeywords = listOf(
			"constructor",
			"vararg",
			"struct",
			"operator",

			"or", "and",

			"plus", "minus",
			"times", "div",

			"rem",
			"pow", "sqrt"
	)

	val DeclarationModifiers = mapOf<Any, List<String>>(
			AstFunction::class to listOf("operator"),
			AstClass::class to listOf("struct"),
			AstFunctionParameter::class to listOf("val", "var", "vararg")
	)
}