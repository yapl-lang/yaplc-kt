package yapl.common

val Char.Companion.NULL get() = 0.toChar()

fun Char.isNull() = this == Char.NULL
fun Char.isNewline() = this == '\r' || this == '\n'
