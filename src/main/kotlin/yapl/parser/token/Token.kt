package yapl.parser.token

import yapl.common.Bound
import kotlin.reflect.KClass

abstract class Token {
	open val isWhitespace get() = false

	abstract val bound: Bound
}

val <T : Token> KClass<T>.tokenType get() = this.simpleName?.removePrefix("Token")
val <T : Token> T?.tokenType get() = if (this == null) "nothing" else this::class.tokenType
