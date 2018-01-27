package yapl.evaluator

abstract class Type(val name: String) : Value(null) {
	override fun toString() = "@Type($name)"
}

object TypeArray : Type("yapl.type.Array")
object TypeBoolean : Type("yapl.type.Boolean")
object TypeFunction : Type("yapl.type.Function")
object TypeNull : Type("yapl.type.Null")
object TypeNumber : Type("yapl.type.Number")
object TypeReference : Type("yapl.type.Reference")
object TypeString : Type("yapl.type.String")
object TypeVoid : Type("yapl.type.Void")
