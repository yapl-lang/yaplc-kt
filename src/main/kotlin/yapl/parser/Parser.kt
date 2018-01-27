package yapl.parser

import yapl.common.Bound
import yapl.common.Position
import yapl.parser.ast.*
import yapl.parser.operator.Operator
import yapl.parser.operator.OperatorSet
import yapl.parser.operator.binaryOperators
import yapl.parser.token.*
import kotlin.reflect.KClass

class Parser(private val tokens: TokenStream) {
	private interface ParseContext {
		fun enter() {}
	}

	private var currentContext: ParseContext = object : ParseContext {}


	private inline fun <reified T : Token> expected(
			offset: Int = 0, skipWhitespace: Boolean = true, failMessage: (Token?) -> String =
			{ "Expected ${T::class.tokenType}, got ${it.tokenType}" },
			condition: (T) -> Boolean = { true }): T {
		val next = tokens.next(offset, skipWhitespace)
		if (!condition((next as? T) ?: error(failMessage(next)))) {
			error(failMessage(next))
		}
		return next
	}

	private inline fun <reified T : Token> take(offset: Int = 0, skipWhitespace: Boolean = true,
	                                            condition: (T) -> Boolean = { true }): T? {
		val tok = tokens.peek(offset, skipWhitespace) as? T ?: return null
		if (condition(tok)) {
			tokens.next(offset, skipWhitespace)
			return tok
		}

		return null
	}

	private inline fun <reified T : Token> skip(offset: Int = 0, skipWhitespace: Boolean = true,
	                                            condition: (T) -> Boolean = { true }): Boolean {
		return condition(tokens.peek(offset, skipWhitespace) as? T ?: return false).also {
			if (it) tokens.next(offset, skipWhitespace)
		}
	}

	private inline fun <reified T : Token> nextIs(offset: Int = 0, skipWhitespace: Boolean = true,
	                                              condition: (T) -> Boolean = { true }): Boolean {
		return condition(tokens.peek(offset, skipWhitespace) as? T ?: return false)
	}

	private inline fun <reified T : Token> skipWhitespace(condition: (T) -> Boolean = { true }): Boolean {
		tokens.push()

		while (true) {
			val next = tokens.next(skipWhitespace = false)
			if (next is T && condition(next)) {
				tokens.release()
				return true
			}
			if (next?.isWhitespace != true) {
				tokens.pop()
				return false
			}
		}
	}

	private fun expectedKeyword(vararg values: String, offset: Int = 0, skipWhitespace: Boolean = true): TokenKeyword {
		assert(values.isNotEmpty())
		return expected(offset, skipWhitespace, {
			if (values.size == 1)
				"Expected keyword: '${values[0]}'"
			else
				"Expected one of keyword: ${values.joinToString { "'$it'" }}"
		}) { values.contains(it.value) }
	}

	private fun expectedOptionalKeyword(vararg values: String, offset: Int = 0, skipWhitespace: Boolean = true): TokenOptionalKeyword {
		assert(values.isNotEmpty())
		return expected(offset, skipWhitespace, {
			if (values.size == 1)
				"Expected optional keyword: '${values[0]}'"
			else
				"Expected one of optional keyword: ${values.joinToString { "'$it'" }}"
		}) { values.contains(it.value) }
	}

	private fun expectedPunctuation(vararg values: Char, offset: Int = 0, skipWhitespace: Boolean = true): TokenPunctuation {
		assert(values.isNotEmpty())
		return expected(offset, skipWhitespace, {
			if (values.size == 1)
				"Expected punctuation: '${values[0]}'"
			else
				"Expected one of punctuation: ${values.joinToString { "'$it'" }}"
		}) { values.contains(it.value) }
	}

	private fun error(message: String, begin: Position = tokens.position, end: Position = begin): Nothing {
		throw ParseError(message, Bound(begin, end))
	}

	class EnterableParseContext : ParseContext {
		var entered = false

		override fun enter() {
			entered = true
		}
	}

	private fun <T : AstNode> parseOptional(method: () -> T): T? {
		val oldContext = currentContext
		val context = EnterableParseContext().also { currentContext = it }

		return try {
			tokens.push { method() }
		} catch (e: ParseError) {
			if (context.entered) {
				oldContext.enter()
				throw e
			}

			null
		} finally {
			currentContext = oldContext
		}
	}

	private fun <T : AstNode> parseMultiple(method: () -> T): AstNodeList<T> =
			mutableListOf<T>().apply { while (true) add(parseOptional(method) ?: break) }

	private fun <T : AstNode> parse(enter: Boolean = false, body: ParseContext.() -> T): T {
		val beginToken = tokens.peek(skipWhitespace = false)
		val begin = tokens.position
		if (enter) currentContext.enter()
		val tok = currentContext.body()
		val endToken = tokens.peek(skipWhitespace = false)
		val end = tokens.position
		tok.apply {
			tokens = this@Parser.tokens.tokensBetween(beginToken, endToken)
			bound = Bound(begin, end)
		}
		return tok
	}


	private fun <T : AstNode> parseDelimited(method: () -> T,
	                                         required: Boolean = true,
	                                         commaDelimiter: Boolean = true,
	                                         mayEmitValueAfterComma: Boolean = false,
	                                         newlineDelimiter: Boolean = true): List<T> {
		val first = (if (required) method() else parseOptional(method)) ?: return emptyList()
		val nodes = mutableListOf(first)

		while (true) {
			if (commaDelimiter && skip<TokenPunctuation> { it.value == ',' }) {
				val node = (if (mayEmitValueAfterComma) parseOptional(method) else method()) ?: break
				nodes.add(node)
				continue
			}
			if (newlineDelimiter && skipWhitespace<TokenNewline>()) {
				val node = parseOptional(method) ?: break
				nodes.add(node)
				continue
			}
			break
		}

		return nodes
	}


	fun parseDotDelimitedName() = parse {
		val entries = mutableListOf<String>()
		entries.add(expected<TokenIdentifier>().value)
		while (skip<TokenPunctuation> { it.value == '.' }) {
			entries.add(expected<TokenIdentifier>().value)
		}
		AstDotDelimitedName(entries)
	}

	fun parseName() = parse {
		AstName(take<TokenOptionalKeyword>()?.value ?: expected<TokenIdentifier>().value)
	}

	fun parsePackage() = parse {
		expectedKeyword("package")
		enter()

		AstPackage(value = parseDotDelimitedName())
	}

	fun parseImport(requireImportKeyword: Boolean = true): AstAbstractImport = parse {
		if (requireImportKeyword) expectedKeyword("import")
		enter()

		val entries = mutableListOf<String>()
		entries.add(expected<TokenIdentifier>().value)
		while (skip<TokenPunctuation> { it.value == '.' }) {
			val next = tokens.next()

			when (next) {
				is TokenPunctuation -> {
					if (next.value != '{') {
						TODO("Unexpected")
					}
					val children = parseDelimited({ parseImport(false) })
					expectedPunctuation('}')
					return@parse AstImportGroup(AstDotDelimitedName(entries), children)
				}
				is TokenIdentifier -> entries.add(next.value)
				else -> TODO("Unexpected")
			}
		}

		AstImport(AstDotDelimitedName(entries),
				if (skip<TokenKeyword> { it.value == "as" }) parseName() else null
		)
	}

	fun parseVariableDeclaration() = parse {
		val modifiers = parseDeclarationModifiers(AstVariableDeclaration::class)
		val isFinal = expectedKeyword("val", "var").value == "val"
		enter()

		val name = parseName()
		val type = if (skip<TokenPunctuation> { it.value == ':' }) parseTypeReference() else null
		val initializer = if (skip<TokenPunctuation> { it.value == '=' }) parseExpression() else null

		AstVariableDeclaration(modifiers, name, type, initializer, isFinal)
	}

	fun parseFunctionParameter() = parse {
		val modifiers = parseDeclarationModifiers(AstFunctionParameter::class)
		val name = parseName()
		enter()

		val type = if (skip<TokenPunctuation> { it.value == ':' }) parseTypeReference() else null
		val value = if (skip<TokenPunctuation> { it.value == '=' }) parseExpression() else null

		AstFunctionParameter(modifiers, name, type, value)
	}

	fun parseDeclarationModifier(available: List<String>) = parse {
		val kw = take<TokenOptionalKeyword> { available.contains(it.value) }?.value
				?: expected<TokenKeyword> { available.contains(it.value) }.value
		AstDeclarationModifier(kw)
	}

	fun <T : AstDeclaration> parseDeclarationModifiers(clazz: KClass<T>) = parse {
		val availableModifiers = Presets.DeclarationModifiers[clazz]
				?: return@parse AstDeclarationModifiers()

		AstDeclarationModifiers(
				parseMultiple { parseDeclarationModifier(availableModifiers) }
		)
	}

	fun parseDeclaration(): AstDeclaration =
			parseOptional { parseVariableDeclaration() }
					?: parseOptional { parseFunction() }
					?: parseOptional { parseClass() }
					?: error("Expected declaration")

	fun parseFunctionParameters() = if (skip<TokenPunctuation> { it.value == '(' }) {
		parseDelimited(::parseFunctionParameter,
				false,
				mayEmitValueAfterComma = true).also { expectedPunctuation(')') }
	} else emptyList()

	fun parseFunctionReturnTypes() = if (skip<TokenPunctuation> { it.value == '-' }) {
		expected<TokenPunctuation> { it.value == '>' }
		parseDelimited(::parseTypeReference)
	} else listOf()

	fun parseFunctionBody() = when {
		skip<TokenPunctuation> { it.value == '=' } -> parseExpression()
		nextIs<TokenPunctuation> { it.value == '{' } -> parseBlockExpression()
		else -> null
	}

	fun parseFunction(requireFunKeyword: Boolean = true, isExpression: Boolean = false) = parse {
		val modifiers = if (requireFunKeyword)
			parseDeclarationModifiers(AstFunction::class).also { expectedKeyword("fun") }
		else
			AstDeclarationModifiers()
		enter()

		val name = parseOptional(::parseName)
		// TODO: Unary operators
		val operator = if (modifiers.has("operator")) selectOperator(binaryOperators) else null
		val parameters = parseFunctionParameters()
		val returnTypes = parseFunctionReturnTypes()
		val body = parseFunctionBody()

		AstFunction(
				modifiers,
				name,
				operator,
				parameters = parameters,
				returnTypes = returnTypes,
				body = body,
				isExpression = isExpression
		)
	}

	fun parsePrimaryConstructor() = parse {
		val modifiers = parseDeclarationModifiers(AstClassConstructor::class)
		val parameters = parseFunctionParameters()

		AstClassConstructor(modifiers, null, parameters)
	}

	fun parseConstructor() = parse {
		val modifiers = parseDeclarationModifiers(AstClassConstructor::class)
		expectedOptionalKeyword("constructor")
		enter()

		val name = parseOptional(::parseName)
		val parameters = parseFunctionParameters()
		val body = parseFunctionBody()

		AstClassConstructor(modifiers, name, parameters, body)
	}

	fun parseClass() = parse {
		val modifiers = parseDeclarationModifiers(AstClass::class)
		expectedKeyword("class")
		enter()

		val name = parseName()
		val primaryConstructor = parseOptional(::parsePrimaryConstructor)
		val parent = if (skip<TokenPunctuation> { it.value == ':' })
			(parseExpression() as? AstCallExpression) ?: error("Superclass should be a call")
		else null
		val members = if (skip<TokenPunctuation> { it.value == '{' })
			parseMultiple { parseOptional(::parseConstructor) ?: parseDeclaration() }
					.also { expected<TokenPunctuation> { it.value == '}' } }
		else
			emptyList()

		AstClass(modifiers, name, parent, primaryConstructor, members)
	}


	fun parseArgument() = parse {
		val name = tokens.push {
			val name = parseOptional(::parseName)
			if (name == null)
				null
			else if (!skip<TokenPunctuation> { it.value == ':' }) {
				pop()
				null
			} else name
		}

		AstArgument(name, parseExpression())
	}


	fun parseIf() = parse {
		expectedKeyword("if")
		enter()

		val condition = parseExpression()

		expectedKeyword("then")
		val thenBlock = parseExpression()

		val elseBlock = if (skip<TokenKeyword> { it.value == "else" }) parseExpression() else null

		AstIfExpression(condition, thenBlock, elseBlock)
	}

	fun parseReturn() = parse {
		expectedKeyword("return")
		enter()

		val expressions = parseDelimited(::parseExpression, newlineDelimiter = false)

		AstReturnExpression(expressions)
	}

	private inline fun parseMaybeCall(crossinline call: () -> AstExpression) = parse {
		val exp = call()

		// NOTE: Do not use push {}, because it falls down
		tokens.push()
		if (skipWhitespace<TokenNewline>()) {
			tokens.pop()
			return@parse exp
		}
		tokens.release()

		val arguments = if (skip<TokenPunctuation> { it.value == '(' }) {
			parseDelimited(::parseArgument, false, mayEmitValueAfterComma = true).also {
				expectedPunctuation(')')
			}
		} else null

		val lambda = if (nextIs<TokenPunctuation> { it.value == '{' }) {
			parseFunction(requireFunKeyword = false, isExpression = true)
		} else null

		if (arguments == null && lambda == null) exp
		else AstCallExpression(exp, arguments ?: emptyList(), lambda)
	}

	private fun <O : Operator> selectOperator(operators: OperatorSet<O>): O? {
		fun charFilter(it: TokenPunctuation) = Presets.OperatorChars.contains(it.value)

		tokens.push {
			val first = take(condition = ::charFilter)?.value
			if (first == null) {
				pop()
				return null
			}

			var possibleOperators = operators.normal.filterKeys { it[0] == first }
			var i = 1
			while (possibleOperators.size != 1) {
				val next = (take(skipWhitespace = false, condition = ::charFilter) ?: break).value
				possibleOperators = possibleOperators.filterKeys { it.length > i && it[i] == next }
				++i
			}
			val operator = possibleOperators.filterKeys { it.length == i }.values.firstOrNull()
			if (operator == null) pop()
			return operator
		}
	}

	private fun parseMaybeBinary(left: AstExpression, myPrecedence: Int): AstExpression = parse {
		tokens.push {
			val operator = selectOperator(binaryOperators) ?: return@push pop()
			if (operator.precedence <= myPrecedence) return@push pop()

			val right = parseMaybeBinary(parseAtom(), operator.precedence)
			return@parse parseMaybeBinary(AstBinaryOperator(left, right, operator), myPrecedence)
		}

		tokens.push {
			tokens.push()
			val isnl = skipWhitespace<TokenNewline>()
			tokens.pop()

			if (!isnl) {
				val isInverted = skip<TokenPunctuation> { it.value == '!' }
				val method = AstReferenceExpression(parseOptional(::parseName) ?: return@push pop())
				val operator = binaryOperators.named[method.name.value]

				val precedence = operator?.precedence ?: 2
				if (precedence <= myPrecedence) return@push pop()

				val right = parseMaybeBinary(parseAtom(), precedence)

				val leftExp = if (operator == null)
					AstBinaryCall(left, right, method, isInverted)
				else
					AstBinaryOperator(left, right, operator, isInverted)

				return@parse parseMaybeBinary(leftExp, myPrecedence)
			}
		}

		return@parse left
	}

	fun parseAtom(): AstExpression = parseMaybeCall {
		if (skip<TokenPunctuation> { it.value == '(' })
			parseExpression().also { expectedPunctuation(')') }
		else take<TokenStringLiteral>()?.run { AstStringLiteralExpression(value) }
				?: take<TokenCharLiteral>()?.run { AstCharLiteralExpression(value) }
				?: take<TokenNumberLiteral>()?.run { AstNumberLiteralExpression(beforeComma, afterComma, afterE) }
				?: parseOptional { AstReferenceExpression(parseName()) }
				?: parseOptional(::parseBlockExpression)
				?: parseOptional(::parseIf)
				?: parseOptional(::parseReturn)
				?: parseOptional { parseDeclaration() }
				?: error("Unexpected")
	}

	fun parseExpression(): AstExpression = parseMaybeCall {
		parseMaybeBinary(parseAtom(), 0)
	}

	fun parseBlockExpression() = parse {
		expectedPunctuation('{')
		enter()

		val expressions = mutableListOf<AstExpression>()

		while (skip<TokenSemicolon>());
		do expressions.add(parseOptional(::parseExpression) ?: continue)
		while (skip<TokenSemicolon>() || skipWhitespace<TokenNewline>())

		expectedPunctuation('}')

		AstCompoundExpression(expressions)
	}

	fun parseNamedTypeReference() = parse { AstNamedTypeReference(parseName()) }

	fun parseMaybeVariantTypeReference(): AstTypeReference = parse {
		fun parseSimplerTypeReference() = parseNamedTypeReference()

		val first = parseSimplerTypeReference()
		val variants = mutableListOf(first)

		while (skip<TokenPunctuation> { it.value == '|' }) {
			variants.add(parseSimplerTypeReference())
		}

		if (variants.size == 1) first else AstVariantType(variants)
	}

	fun parseTypeReference(): AstTypeReference = parseMaybeVariantTypeReference()

	fun parse() = parse(true) {
		AstProgram(
				`package` = parseOptional(::parsePackage),
				imports = parseMultiple { parseImport() },
				declarations = parseMultiple { parseDeclaration() }
		)
	}
}