package yapl.parser

import yapl.parser.ast.AstNode
import yapl.parser.ast.AstNodeList
import java.io.Writer
import kotlin.reflect.KProperty1
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

class AstDebugPrinter(val out: Writer, val indent: String = "\t") {
	companion object {
		val restrictedKeys = listOf(
				"typeName",
				"tokens",
				"bound"
		)
	}

	private var wasNl = true
	private var indentSize = 0

	inline private fun indent(block: () -> Unit) {
		try {
			++indentSize
			block()
		} finally {
			--indentSize
		}
	}

	private fun print(vararg values: Any?) {
		if (wasNl) {
			wasNl = false
			repeat(indentSize) { out.write(indent) }
		}
		values.forEach { out.write(it.toString()) }
	}

	private fun println(vararg values: Any?) {
		print(*values)
		out.write("\n")
		wasNl = true
	}

	fun print(node: AstNode) {
		val clazz = node::class

		val bound = try {
			node.bound.toString()
		} catch (e: UninitializedPropertyAccessException) {
			"?"
		}

		println(node.typeName, " ", bound)
		indent {
			if (!clazz.isData) println("// ${node.typeName} is not a data class")
			clazz.memberProperties
					.filter { it.visibility == KVisibility.PUBLIC }
					.filter { !restrictedKeys.contains(it.name) }
					.filterIsInstance<KProperty1<AstNode, *>>()
					.forEach { field ->
						val name = field.name
						val value: Any? = field.get(node)

						print(name, " => ")
						when (value) {
							is AstNodeList<*> -> {
								println("NodeList")
								indent {
									value.forEach {
										if (it is AstNode) print(it) else println(it)
									}
								}
							}
							is AstNode        -> print(value)
							else              -> println(value)
						}
					}
		}
	}
}
