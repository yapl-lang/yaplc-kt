package yapl.evaluator

import yapl.parser.ast.AstProgram

interface Importer {
	fun import(fullName: String): AstProgram?
}
