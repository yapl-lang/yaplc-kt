package yapl.evaluator

import yapl.parser.ast.*
import yapl.parser.operator.BinaryOperator
import java.math.BigDecimal

class Evaluator(vararg val importers: Importer) {
	private val binaryOperatorEvaluator = BinaryOperatorEvaluator(this)

	val rootEnv = Environment().apply {
		createVariable("Array", TypeArray)
		createVariable("Boolean", TypeBoolean)
		createVariable("Function", TypeFunction)
		createVariable("Null", TypeNull)
		createVariable("Number", TypeNumber)
		createVariable("String", TypeString)
		createVariable("Void", TypeVoid)

		createVariable("print", ValueInternalFunction(AstFunction(
				parameters = listOf(AstFunctionParameter(AstDeclarationModifiers(listOf(
						AstDeclarationModifier("vararg")
				)), AstName("message"))))) { funEnv ->
			(funEnv.getVariable("message") as ValueArray).value.forEach { print(it) }
			ValueVoid
		})
		createVariable("println", ValueInternalFunction(AstFunction(
				parameters = listOf(AstFunctionParameter(AstDeclarationModifiers(listOf(
						AstDeclarationModifier("vararg")
				)), AstName("message"))))) { funEnv ->
			(funEnv.getVariable("message") as ValueArray).value.forEach { print(it) }
			println()
			ValueVoid
		})
	}

	private fun Environment.applyImportGroup(import: AstImportGroup, path: String = "") {
		val newPath = (if (path.isEmpty()) "" else "$path.") + import.value.value
		import.imports.forEach {
			when (it) {
				is AstImport      -> applyImport(it, newPath)
				is AstImportGroup -> applyImportGroup(it, newPath)
			}
		}
	}

	private fun Environment.applyImport(import: AstImport, path: String = "") {
		val realName = import.value.entries.last()

		val name = import.alias?.value ?: realName
		val importPath = (if (path.isEmpty()) "" else "$path.") + import.value.value
		val importValue = import(importPath)?.getVariable(realName)
				?: throw RuntimeException("Failed to resolve import $name")

		setVariable(name, importValue)
	}

	private fun Environment.applyDeclaration(declaration: AstDeclaration) {
		val name = declaration.name ?: return
		when (declaration) {
			is AstClass    -> TypeClass(this@Evaluator, this, declaration).let {
				setLocalVariable("this", it)
				createVariable(name.value, it)
			}
			is AstFunction -> createVariable(name.value, ValueFunction(this, declaration))
			else           -> println("Unknown declaration type ${declaration::class.simpleName}")
		}
	}

	private fun createEnv(program: AstProgram) = rootEnv.extend().apply {
		program.imports.forEach {
			when (it) {
				is AstImport      -> applyImport(it)
				is AstImportGroup -> applyImportGroup(it)
			}
		}

		program.declarations.forEach { applyDeclaration(it) }
	}

	fun import(fullName: String): Environment? {
		return importers
				.mapNotNull { it.import(fullName) }
				.firstOrNull()?.let { createEnv(it) }
	}

	fun checkType(value: Value, type: Type): Boolean {
		val valueType = value.type ?: return false
		if (type == valueType) return true

		if (valueType is TypeClass && type is TypeClass) {
			var t: TypeClass? = valueType
			while (t != null) {
				if (type == t) return true
				t = t.parent
			}
		}

		return false
	}

	fun Environment.checkType(value: Value, type: AstTypeReference): Boolean {
		return when (type) {
			is AstVariantType        -> type.variants.any { checkType(value, type) }
			is AstNamedTypeReference -> {
				val typeVar = getVariable(type.value.value)
				val type = when (typeVar) {
					null    -> throw RuntimeException("Variable does not exist")
					is Type -> typeVar
					else    -> if (typeVar is ValueClass)
						typeVar.type!!
					else
						throw RuntimeException("The value is not a type")
				}
				checkType(value, type)
			}
			else                     -> false
		}
	}

	fun assertType(value: Value, type: Type) {
		if (!checkType(value, type)) throw RuntimeException("TypeError")
	}

	fun Environment.assertType(value: Value, type: AstTypeReference) {
		if (!checkType(value, type)) throw RuntimeException("TypeError")
	}

	fun unref(value: Value): Value = when (value) {
		is ValueReference -> unref(value.value)
		else              -> value
	}

	fun unrefNullable(value: Value?): Value? = if (value == null) null else unref(value)

	fun Environment.instantiate(call: AstCallExpression, callee: TypeClass, obj: ValueClass) {
		callee.parent?.let { instantiate(call, it, obj) }
		val constructor = callee.declaration.primaryConstructor ?: throw RuntimeException("Has no error")

		val env = Environment(this)
		assignParams(this, env, call.arguments, constructor.parameters, call.lambda)

		constructor.parameters
				.filter { it.modifiers.has("val") || it.modifiers.has("var") }
				.forEach { obj.members[it.name.value] = env.getLocalVariable(it.name.value)!! }
	}

	fun Environment.instantiate(call: AstCallExpression,
	                            callee: TypeClass) = ValueClass(this, callee).also {
		instantiate(call, callee, it)
	}

	fun assignParams(evalEnv: Environment, funcEnv: Environment,
	                 args: AstNodeList<AstArgument>,
	                 parameters: AstNodeList<AstFunctionParameter>,
	                 lambda: AstFunction? = null) {
		val arguments = args.map({ Pair(it, unref(evalEnv.evaluate(it.value))) })
		val usedParams = mutableSetOf<AstFunctionParameter>()

		arguments
				.filter { it.first.name != null }
				.forEach {
					val (key, value) = it
					val name = key.name!!.value
					val param = parameters.first { it.name.value == name }
					if (!usedParams.add(param)) {
						throw RuntimeException("Already used $name")
					}
					param.type?.let { funcEnv.assertType(value, it) }
					funcEnv.createVariable(name, value)
				}

		if (lambda != null) {
			val lastParam = parameters.last()
			if (!usedParams.add(lastParam)) throw RuntimeException("Already used ${lastParam.name.value}")

			val func = ValueFunction(evalEnv, lambda)
			lastParam.type?.let { funcEnv.assertType(func, it) }
			funcEnv.createVariable(lastParam.name.value, func)
		}

		val ordinalArguments = arguments.filter { it.first.name == null }
		parameters.listIterator().let {
			val argsIt = ordinalArguments.iterator()

			while (it.hasNext() && argsIt.hasNext()) {
				var next = it.next()
				if (next.modifiers.has("vararg")) {
					usedParams.add(next)

					val value = argsIt.next().second
					next.type?.let { funcEnv.assertType(value, it) }

					val args = funcEnv.getLocalVariable(next.name.value) as ValueArray?
					val result = arrayOf(*(args?.value ?: emptyArray()), value)
					funcEnv.setLocalVariable(next.name.value, ValueArray(result))
					it.previous()
				} else {
					while (!usedParams.add(next) && it.hasNext()) next = it.next()

					val value = argsIt.next().second
					next.type?.let { funcEnv.assertType(value, it) }
					funcEnv.createVariable(next.name.value, value)
				}
			}
		}

		parameters
				.filter { !usedParams.contains(it) }
				.forEach {
					if (it.default == null) {
						if (!it.modifiers.has("vararg"))
							throw RuntimeException("Argument ${it.name.value} is not specified")
						funcEnv.createVariable(it.name.value, ValueArray(emptyArray()))
					} else {
						funcEnv.createVariable(it.name.value, unref(evalEnv.evaluate(it.default)))
					}
				}
	}

	fun Environment.call(call: AstCallExpression,
	                     calleeValue: Value = evaluate(call.callee),
	                     receiver: Value? = null): Value {
		val callee = unref(calleeValue)
		val declaration = when (callee) {
			is ValueFunction         -> callee.function
			is ValueInternalFunction -> callee.declaration
			is ValueBindFunction     -> return call(call, callee.callee, callee.receiver)
			is TypeClass             -> return instantiate(call, callee)
			else                     -> throw RuntimeException("Not a callable")
		}
		val env = when (callee) {
			is ValueFunction         -> callee.env
			is ValueInternalFunction -> this
			else                     -> throw RuntimeException("Cannot happen")
		}
		val newEnv = Environment(env)
		receiver?.let { newEnv.putReceiver(it) }
		assignParams(this, newEnv, call.arguments, declaration.parameters, call.lambda)

		return when (callee) {
			is ValueFunction         -> newEnv.evaluate(callee.function.body!!)
			is ValueInternalFunction -> callee.function(newEnv)
			else                     -> throw RuntimeException("Cannot happen")
		}
	}

	fun Environment.evaluate(expr: AstExpression): Value = when (expr) {
		is AstFunction                -> ValueFunction(this, expr).also { func ->
			expr.name?.let { createVariable(it.value, func) } // TODO: Check
		}
		is AstVariableDeclaration     -> {
			(if (expr.initializer != null) unref(evaluate(expr.initializer)) else ValueNull).let {
				expr.type?.let { type -> assertType(it, type) }
				createVariable(expr.name.value,
						it) ?: throw RuntimeException("Variable already exist")
			}
		}
		is AstReferenceExpression     -> getVariableReference(expr.name.value) ?: throw RuntimeException(
				"Variable ${expr.name.value} is not declared")
		is AstCompoundExpression      -> expr.expressions.map { evaluate(it) }.lastOrNull() ?: ValueVoid
		is AstCallExpression          -> call(expr)
		is AstIfExpression            -> when {
			(evaluate(expr.condition) as ValueBoolean).value -> evaluate(expr.thenBlock)
			expr.elseBlock != null                           -> evaluate(expr.elseBlock)
			else                                             -> ValueVoid
		}
		is AstStringLiteralExpression -> ValueString(expr.value)
		is AstNumberLiteralExpression -> ValueNumber(BigDecimal(expr.beforeComma +
				"." + (expr.afterComma ?: "0") +
				"e" + (expr.afterE ?: "0")))
		is AstBinaryOperator          -> when (expr.operator) {
			BinaryOperator.Member     -> {
				val left = unref(evaluate(expr.left)) as? ValueClass ?: throw RuntimeException("Can't get member not on a class")
				val right = expr.right as? AstReferenceExpression ?: throw RuntimeException("Member name needed")

				ValueMemberReference(left, right.name.value)
			}
			BinaryOperator.Assignment -> {
				val left = evaluate(expr.left) as? ValueReference ?: throw RuntimeException("Left operand is not left-value")
				val right = unref(evaluate(expr.right))

				left.value = right
				right
			}
			else                      -> {
				val left = unref(evaluate(expr.left))
				val right = unref(evaluate(expr.right))

				val result = with(binaryOperatorEvaluator) {
					evaluate(expr.operator, left, right)
							?: throw RuntimeException("Can't evaluate operator")
				}
				// TODO: if (expr.isInverted) value = !value

				result
			}
		}
		else                          -> TODO()
	}
}
