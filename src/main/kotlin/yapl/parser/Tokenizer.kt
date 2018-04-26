package yapl.parser

import yapl.common.*
import yapl.parser.token.*
import kotlin.coroutines.experimental.buildSequence

class Tokenizer {
	fun tokenize(reader: Reader) = TokenStream(buildSequence {
		var begin = reader.position()
		fun bound(): Bound {
			val end = reader.position()
			val bound = Bound(begin, end)
			begin = end

			return bound
		}

		loop@ while (!reader.eof()) {
			val c = reader.next()

			when {
				c.isNewline() -> yield(TokenNewline(bound(), "$c"))
				c.isWhitespace() -> {
					val whitespace = c + reader.takeWhile { it -> it.isWhitespace() }
					yield(TokenWhitespace(bound(), whitespace))
				}
				c == '#' -> yield(TokenLineComment(bound(), reader.takeWhile { it -> !it.isNewline() }))
				c == ';' -> yield(TokenSemicolon(bound(), "$c"))
				c == '\'' -> {
					val char = reader.readMaybeEscaped()
					val bound by lazy { bound() }
					if (reader.peek() != '\'') {
						reader.takeWhile { it -> it != '\'' }
						yield(TokenError(bound, "Character literal should contain only one character"))
					}
					reader.next()
					yield(TokenCharLiteral(bound, char))
				}
				c == '"' -> {
					val value = buildString {
						while (reader.peek() != '"') {
							if (reader.eof()) {
								yield(TokenError(bound(), "Expected closing brace '\"'"))
								break
							}
							append(reader.readMaybeEscaped())
						}
						reader.take('"')
					}
					yield(TokenStringLiteral(bound(), value))
				}
				c.isDigit() -> {
					val beforeComma = c + reader.takeWhile { it -> it.isDigit() }
					var addPunctuation = false
					val afterComma = if (reader.take('.')) {
						if (!reader.peek().isDigit()) {
							addPunctuation = true
							null
						} else reader.takeWhile { it -> it.isDigit() }
					} else null
					val afterE = if (reader.take('e')) reader.takeWhile { it -> it.isDigit() } else null

					yield(TokenNumberLiteral(bound(), beforeComma, afterComma, afterE))
					if (addPunctuation) yield(TokenPunctuation(bound(), '.'))
				}
				c.isJavaIdentifierStart() -> {
					val id = c + reader.takeWhile { it -> it.isJavaIdentifierPart() }

					when {
						Presets.Keywords.contains(id) -> yield(TokenKeyword(bound(), id))
						Presets.OptionalKeywords.contains(id) -> yield(TokenOptionalKeyword(bound(), id))
						else -> yield(TokenIdentifier(bound(), id))
					}
				}
				c == '`' -> {
					val value = buildString {
						while (reader.peek() != '`') {
							if (reader.eof()) {
								yield(TokenError(bound(), "Expected closing brace '`'"))
								break
							}
							append(reader.next())
						}
						reader.take('`')
					}
					yield(TokenIdentifier(bound(), value))
				}
				Presets.Punctuations.contains(c) -> yield(TokenPunctuation(bound(), c))
				else -> {
					yield(TokenError(bound(), "Unexpected '$c'"))
					break@loop
				}
			}
		}
	})
}
