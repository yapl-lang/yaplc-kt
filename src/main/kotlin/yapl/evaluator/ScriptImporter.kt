package yapl.evaluator

import yapl.common.string.StringSource
import yapl.parser.ParseError
import yapl.parser.Parser
import yapl.parser.Tokenizer
import yapl.parser.ast.AstProgram
import java.nio.file.Files
import java.nio.file.Path

class ScriptImporter(val root: Path, val packageRoot: String = "") : Importer {
	override fun import(fullName: String): AstProgram? {
		val `package` = packageRoot + (if (packageRoot.isEmpty()) "" else ".") + fullName
		val path = `package`.replace(".", root.fileSystem.separator) + ".yapl"

		val source = StringSource(Files.readAllLines(root.resolve(path)).joinToString("\n"))
		val reader = source.createReader()
		val tokens = Tokenizer().tokenize(reader)

		try {
			val ast = Parser(tokens).parse()
			ast.tokens.lastOrNull()?.let {
				println("Stop at ${it.bound}")
			}
			return ast
		} catch (e: ParseError) {
			println("Error ${e.bound}: ${e.message}")
			throw e
		}
	}
}
