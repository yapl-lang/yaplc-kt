package yapl.test

import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.jetbrains.spek.subject.SubjectSpek
import yapl.common.Reader
import yapl.common.Source
import yapl.common.isNewline
import yapl.common.string.StringSource
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

object StringReader : SubjectSpek<Reader>({
	val text = """
			HelloWorld
			HelloWorld
			HelloWorld
		""".trimIndent()
	val source: Source = StringSource(text)

	subject { source.createReader() }

	it("should start from position(1, 0)") {
		val position = subject.position()

		assertEquals(1, position.line)
		assertEquals(0, position.column)
	}

	on("eof") {
		it("should not be on start") {
			assertFalse(subject.eof())
		}

		it("should be when there are no text") {
			for (char in text) {
				subject.next()
			}

			assertTrue(subject.eof())
		}
	}

	on("eol") {
		it("should not be on start") {
			assertFalse(subject.eol())
		}

		it("should be when char is newline") {
			for (char in text) {
				val pos = subject.position()
				assertTrue(subject.eol() || !char.isNewline(), pos.toString(false))

				subject.next()
			}
		}
	}

	describe("peek") {
		it("should return the same result after two calls together") {
			assertEquals(subject.peek(), subject.peek())
		}

		it("should not change position after a call") {
			val position1 = subject.position()
			subject.peek()
			val position2 = subject.position()

			assertEquals(position1, position2)
		}

		it("should return the correct char") {
			assertEquals(text[0], subject.peek())
		}
	}

	describe("next") {
		it("should change column by one after a call") {
			val position1 = subject.position()
			subject.next()
			val position2 = subject.position()

			assertEquals(1, position2.column - position1.column)
		}

		it("should print right text") {
			for (char in text) {
				val pos = subject.position()
				assertEquals(char, subject.next(), pos.toString(false))
			}
		}
	}
})
