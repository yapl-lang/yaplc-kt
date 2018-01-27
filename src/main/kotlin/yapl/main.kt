package yapl

import yapl.evaluator.Evaluator
import yapl.evaluator.ScriptImporter
import yapl.parser.ast.AstCallExpression
import yapl.parser.ast.AstName
import yapl.parser.ast.AstReferenceExpression
import java.nio.file.Paths

fun main(vararg args: String) {
	val evaluator = Evaluator(ScriptImporter(Paths.get(args[0])))
	val env = evaluator.import(args[1]) ?: throw RuntimeException()
	val method = args[2]

	with (evaluator) {
		with (env) {
			evaluate(AstCallExpression(AstReferenceExpression(AstName(method))))
		}
	}
}
