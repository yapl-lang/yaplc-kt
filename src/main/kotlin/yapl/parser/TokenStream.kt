package yapl.parser

import yapl.parser.token.Token
import java.util.*

class TokenStream(sequence: Sequence<Token>) {
	private val sequenceIterator = sequence.iterator()
	private val cachedTokens = mutableListOf<Token>()

	private var offset: Int = 0
	private val stack = Stack<Int>()

	val position get() = peek()?.bound?.begin ?: cachedTokens.last().bound.end

	fun peek(offset: Int = 0, skipWhitespace: Boolean = true): Token? {
		var counter = offset + 1

		return cachedTokens
			.drop(this.offset)
			.firstOrNull { (!it.isWhitespace || !skipWhitespace) && --counter == 0 } ?: run {
			while (true) {
				if (!sequenceIterator.hasNext()) {
					return@run null
				}
				val tok = sequenceIterator.next()
				cachedTokens.add(tok)
				if ((!tok.isWhitespace || !skipWhitespace) && --counter == 0) {
					return@run tok
				}
			}
		} as Token?
	}

	fun next(offset: Int = 0, skipWhitespace: Boolean = true): Token? {
		return peek(offset, skipWhitespace)?.also { it ->
			val index = cachedTokens.indexOf(it)
			assert(index != -1)
			assert(index > this.offset)
			this.offset = index + 1
		}
	}

	fun skip(count: Int = 1, skipWhitespace: Boolean = true) {
		peek(offset = count - 1, skipWhitespace = skipWhitespace)?.also { it ->
			val index = cachedTokens.indexOf(it)
			assert(index != -1)
			assert(index > offset)
			offset = index + 1
		}
	}


	fun push() {
		stack.push(offset)
	}

	inner class TokenStreamContext {
		var done = false
			private set

		fun pop() {
			done = true
			this@TokenStream.pop()
		}

		fun release() {
			done = true
			this@TokenStream.release()
		}
	}

	inline fun <T> push(callback: TokenStreamContext.() -> T): T {
		val context = TokenStreamContext()
		push()
		try {
			val value = context.callback()
			if (!context.done) {
				release()
			}
			return value
		} catch (e: Throwable) {
			pop()
			throw e
		}
	}

	fun pop() {
		offset = stack.pop()
	}

	fun release() {
		stack.pop()
	}


	fun tokensBetween(beginToken: Token?, endToken: Token?): List<Token> {
		if (beginToken == null) {
			return emptyList()
		}

		val begin = cachedTokens.indexOf(beginToken)
		val end = if (endToken != null) cachedTokens.indexOf(endToken) - 1 else cachedTokens.size - 1
		assert(begin != -1)
		assert(end != -1)
		return cachedTokens.slice(begin..end)
	}
}
