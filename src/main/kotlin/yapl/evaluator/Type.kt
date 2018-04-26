package yapl.evaluator

abstract class Type(val name: String) : Value(TypeType) {
	override fun toString() = "@Type($name)"
}

object TypeType : Type("spela.type.Type")
object TypeArray : Type("spela.type.Array")
object TypeBoolean : Type("spela.type.Boolean")
object TypeFunction : Type("spela.type.Function")
object TypeNothing : Type("spela.type.Nothing")
object TypeNull : Type("spela.type.Null")
object TypeNumber : Type("spela.type.Number")
object TypeReference : Type("spela.type.Reference")
object TypeString : Type("spela.type.String")
object TypeVoid : Type("spela.type.Void")
