package yapl.common

interface Position {
    val source: Source?

    val column: Int
    val line: Int


	override fun toString(): String
	fun toString(source: Boolean = true): String
}
