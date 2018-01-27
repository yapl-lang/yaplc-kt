package yapl.common

interface Source {
	fun createReader(): Reader

	override fun toString(): String
}
