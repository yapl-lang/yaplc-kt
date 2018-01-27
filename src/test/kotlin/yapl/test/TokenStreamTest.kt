package yapl.test

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import yapl.common.string.StringReader
import yapl.common.string.StringSource
import yapl.parser.Tokenizer
import yapl.parser.token.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object TokenStreamTest : Spek({
	fun create(text: String) = Tokenizer().tokenize(StringReader(StringSource(text)))

	on("peek") {
		it("should return the same token after two calls") {
			val subject = create("token")
			assertTrue(subject.peek() === subject.peek())
		}
	}

	on("next") {
		it("should return different tokens after two calls") {
			val subject = create("token token")
			assertTrue(subject.next() !== subject.next())
		}
	}

	on("push/pop") {
		it("should save and restore position with push/pop calls") {
			val subject = create("token token token")

			lateinit var a: Token
			lateinit var b: Token
			lateinit var c: Token

			subject.push {
				a = subject.next()!!
				b = subject.next()!!
				c = subject.next()!!
			}

			assertTrue(a === subject.next()!!)
			assertTrue(b === subject.next()!!)
			assertTrue(c === subject.next()!!)
		}
	}

	group("tokenizing") {
		it("should tokenize identifier") {
			val subject = create("sometoken")
			val tok = subject.peek()
			assertTrue(tok is TokenIdentifier)
			assertEquals("sometoken", (tok as TokenIdentifier).value)
		}

		it("should tokenize keyword") {
			val subject = create("package")
			val tok = subject.peek()
			assertTrue(tok is TokenKeyword)
			assertEquals("package", (tok as TokenKeyword).value)
		}

		it("should tokenize line comment") {
			val subject = create("# ABCABC")
			val tok = subject.peek(skipWhitespace = false)
			assertTrue(tok is TokenLineComment)
			assertEquals(" ABCABC", (tok as TokenLineComment).value)
		}

		it("should tokenize new line") {
			val subject = create("\n")
			val tok = subject.peek(skipWhitespace = false)
			assertTrue(tok is TokenNewline)
			assertEquals("\n", (tok as TokenNewline).value)
		}

		// TODO:
//		it("should tokenize optional keyword") {
//			val subject = create("")
//			val tok = subject.peek()
//			assertTrue(tok is TokenOptionalKeyword)
//			assertEquals("", (tok as TokenOptionalKeyword).value)
//		}

		it("should tokenize punctuation") {
			val subject = create("()")
			val tok = subject.peek(skipWhitespace = false)
			assertTrue(tok is TokenPunctuation)
			assertEquals('(', (tok as TokenPunctuation).value)
		}

		it("should tokenize semicolon") {
			val subject = create(";")
			val tok = subject.peek(skipWhitespace = false)
			assertTrue(tok is TokenSemicolon)
			assertEquals(";", (tok as TokenSemicolon).value)
		}

		it("should tokenize whitespace") {
			val subject = create("  ")
			val tok = subject.peek(skipWhitespace = false)
			assertTrue(tok is TokenWhitespace)
			assertEquals("  ", (tok as TokenWhitespace).value)
		}
	}
})
