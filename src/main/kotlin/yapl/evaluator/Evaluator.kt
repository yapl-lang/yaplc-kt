package yapl.evaluator

import yapl.parser.ast.*
import yapl.parser.operator.BinaryOperator
import java.io.File
import java.math.BigDecimal

class Evaluator(vararg val importers: Importer) {
	private val binaryOperatorEvaluator = BinaryOperatorEvaluator(this)
	private val prefixOperatorEvaluator = PrefixOperatorEvaluator(this)
	private val imported = mutableMapOf<String, Environment?>()

	val rootEnv = Environment().apply {
		createVariable("Type", TypeType)
		createVariable("Array", TypeArray)
		createVariable("Boolean", TypeBoolean)
		createVariable("Function", TypeFunction)
		createVariable("Nothing", TypeNothing)
		createVariable("Null", TypeNull)
		createVariable("Number", TypeNumber)
		createVariable("String", TypeString)
		createVariable("Char", TypeString)
		createVariable("Void", TypeVoid)

		createVariable("null", ValueNull)
		createVariable("true", ValueBoolean(true))
		createVariable("false", ValueBoolean(false))

		createVariable("print", ValueInternalFunction(AstFunction(
				parameters = listOf(AstFunctionParameter(AstDeclarationModifiers(listOf(
						AstDeclarationModifier("vararg")
				)), AstName("message"))))) { funEnv ->
			(funEnv.getVariable("message") as ValueArray).value.forEach { print(stringRepresent(it)) }
			ValueVoid
		})
		createVariable("println", ValueInternalFunction(AstFunction(
				parameters = listOf(AstFunctionParameter(AstDeclarationModifiers(listOf(
						AstDeclarationModifier("vararg")
				)), AstName("message"))))) { funEnv ->
			(funEnv.getVariable("message") as ValueArray).value.forEach { print(stringRepresent(it)) }
			println()
			ValueVoid
		})

		createVariable("ert_array", ValueInternalFunction(AstFunction(
				parameters = listOf(AstFunctionParameter(AstDeclarationModifiers(),
						AstName("length"))))) { funEnv ->
			ValueArray(Array((funEnv.getVariable("length") as ValueNumber).value.toInt()) {
				ValueNull
			})
		})

		createVariable("ert_readFile", ValueInternalFunction(AstFunction(
				parameters = listOf(AstFunctionParameter(AstDeclarationModifiers(),
						AstName("path"))))) { funEnv ->
			val path = (funEnv.getVariable("path") as ValueString).value
			ValueString(File(path).readText())
		})

		createVariable("ert_apply", ValueInternalFunction(AstFunction(
				parameters = listOf(
						AstFunctionParameter(AstDeclarationModifiers(), AstName("object")),
						AstFunctionParameter(AstDeclarationModifiers(), AstName("callee")),
						AstFunctionParameter(AstDeclarationModifiers(), AstName("label"), default = AstReferenceExpression(AstName("null")))
				))) { funEnv ->
			val `object` = funEnv.getVariable("object") as ValueClass
			val `callee` = funEnv.getVariable("callee")
			val label = (funEnv.getVariable("label") as? ValueString)?.value

			extend().apply {
				call
			}
			//ValueString(File(path).readText())
		})

		createVariable("exit", ValueInternalFunction(AstFunction(
				parameters = listOf(
						AstFunctionParameter(AstDeclarationModifiers(), AstName("code"), default = AstNumberLiteralExpression("0")),
						AstFunctionParameter(AstDeclarationModifiers(), AstName("message"), default = AstReferenceExpression(AstName("null")))
				))) { funEnv ->
			val code = (funEnv.getVariable("code") as ValueNumber).value.toInt()
			val message = (funEnv.getVariable("message") as? ValueString)?.value

			message?.let { println(it) }
			System.exit(code)
			ValueNothing
		})
	}

	private fun Environment.applyImportGroup(import: AstImportGroup, path: String = "") {
		val newPath = "$path${import.value.value}."
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
		val importPath = path + import.value.value
		val importValue = import(importPath)?.getVariable(realName)
				?: throw RuntimeException("Failed to resolve import $name")

		setLocalVariable(name, importValue)
	}

	private fun Environment.createDeclaration(declaration: AstDeclaration) {
		val name = declaration.name ?: return

		createVariable(name.value, evaluate(declaration))
		/*when (declaration) {
			is AstClass -> TypeClass(this, declaration).let {
				setLocalVariable("this", it)
				createVariable(name.value, it)
			}
			is AstFunction -> createVariable(name.value, ValueFunction(this, declaration))
			is AstVariableDeclaration -> evaluate(declaration)
			else -> println("Unknown declaration type ${declaration::class.simpleName}")
		}*/
	}

	private fun Environment.applyDeclaration(declaration: AstDeclaration) {
		val name = declaration.name?.value ?: return
		when (declaration) {
			is AstClass -> {
				(getVariable(name) as TypeClass).run { init() }

				if (declaration.modifiers.has("object")) {
					val instance = instantiate(AstCallExpression(AstReferenceExpression(AstName(""))), getVariable(name) as TypeClass)
					setVariable(name, instance)
				}
			}
		}
	}

	private fun Environment.createEnv(program: AstProgram) {
		program.declarations.forEach { createDeclaration(it) }
	}

	private fun Environment.applyEnv(program: AstProgram) {
		program.imports.forEach {
			when (it) {
				is AstImport      -> applyImport(it)
				is AstImportGroup -> applyImportGroup(it)
			}
		}

		program.declarations.forEach { applyDeclaration(it) }
	}

	fun import(fullName: String): Environment? {
		if (imported.containsKey(fullName)) return imported[fullName]

		val env = rootEnv.extend()
		val program = importers.mapNotNull { it.import(fullName) }.firstOrNull()
		if (program == null) {
			imported[fullName] = null
			return null
		}
		env.createEnv(program)
		imported[fullName] = env
		env.applyEnv(program)
		return env
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
			is AstVariantType        -> type.variants.any { checkType(value, it) }
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

	fun typeRefRepresent(type: AstTypeReference): String = when (type) {
		is AstNamedTypeReference -> type.value.value
		is AstVariantType        -> type.variants.joinToString { typeRefRepresent(it) }
		else                     -> "Unknown type"
	}

	fun assertType(value: Value, type: Type) {
		if (!checkType(value, type)) throw RuntimeException("TypeError")
	}

	fun Environment.assertType(value: Value, type: AstTypeReference) {
		if (!checkType(value, type)) {
			val stringValue = stringRepresent(value)
			val stringType = typeRefRepresent(type)
			throw RuntimeException("TypeError: $stringValue had to be $stringType")
		}
	}

	fun unref(value: Value): Value = when (value) {
		is ValueReference -> unref(value.value)
		else              -> value
	}

	fun unrefNullable(value: Value?): Value? = if (value == null) null else unref(value)

	fun Environment.instantiate(call: AstCallExpression, callee: TypeClass, obj: ValueClass) {
		callee.parent?.let { instantiate(callee.declaration.parent!!, it, obj) }

		val constructor = callee.declaration.primaryConstructor
				?: throw RuntimeException("Has no primary constructor")

		val env = Environment(callee.env)
		env.putReceiver(obj)
		assignParams(this, env, call.arguments, constructor.parameters, call.lambda)

		callee.declaration.members
				.filterIsInstance<AstVariableDeclaration>()
				.forEach {
					val initializer = it.initializer ?: return
					val value = env.evaluate(initializer)
					it.type?.let { env.checkType(value, it) }
					obj.setMember(it.name.value, value)
				}

		constructor.parameters
				.filter { it.modifiers.has("val") || it.modifiers.has("var") }
				.forEach { obj.members[it.name.value] = env.getLocalVariable(it.name.value)!! }

		constructor.body?.let { env.evaluate(it) }

		callee.memberConstructors.firstOrNull()?.let {
			val env2 = Environment(it.first)
			env2.putReceiver(obj)
			assignParams(this, env2, call.arguments, constructor.parameters, call.lambda)
			it.second.body?.let { env2.evaluate(it) }
		}
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
			is ValueFunction         -> newEnv.evaluate(callee.function.body!!) // TODO: Check type
			is ValueInternalFunction -> callee.function(newEnv)
			else                     -> throw RuntimeException("Cannot happen")
		}
	}

	fun Environment.evaluate(expr: AstExpression): Value = when (expr) {
		is AstDeclaration             -> extend().let { env ->
			val value = when (expr) {
				is AstFunction            -> ValueFunction(env, expr)
				is AstClass               -> TypeClass(env, expr).also { setLocalVariable("this", it) }
				is AstVariableDeclaration -> (if (expr.initializer != null) unref(evaluate(expr.initializer)) else ValueNull).also {
					expr.type?.let { type -> env.assertType(it, type) }
				}
				else                      -> TODO()
			}
			expr.name?.let { createVariable(it.value, value) } ?: value // TODO: Check
		}
		is AstReferenceExpression     -> getVariableReference(expr.name.value)
				?: throw RuntimeException(
						"Variable ${expr.name.value} is not declared")
		is AstCompoundExpression      -> expr.expressions.map { evaluate(it) }.lastOrNull()
				?: ValueVoid
		is AstCallExpression          -> call(expr)
		is AstIfExpression            -> when {
			(unref(evaluate(expr.condition)) as ValueBoolean).value -> evaluate(expr.thenBlock)
			expr.elseBlock != null                                  -> evaluate(expr.elseBlock)
			else                                                    -> ValueVoid
		}
		is AstForLoopExpression       -> {
			val variable = expr.variable
			val range = unref(evaluate(expr.range))
			val action = expr.action

			when (range) {
				is ValueClass -> {
					lateinit var hasNextFn: Value
					lateinit var nextFn: Value
					try {
						hasNextFn = range.getMember("hasNext")
						nextFn = range.getMember("next")
					} catch (_: Throwable) {
						val iterator = call(AstCallExpression(AstReferenceExpression(AstName(""))),
								range.getMember("getIterator")) as ValueClass
						hasNextFn = iterator.getMember("hasNext")
						nextFn = iterator.getMember("next")
					}

					var env: Environment = extend()
					fun hasNext() = (unref(env.call(AstCallExpression(AstReferenceExpression(AstName(""))), hasNextFn)) as ValueBoolean).value
					fun next() = env.call(AstCallExpression(AstReferenceExpression(AstName(""))), nextFn)

					fun iteration() = env.run {
						setLocalVariable(variable.value, next())
						evaluate(action)
					}.also { env = extend() }

					var current = if (hasNext()) iteration() else ValueNull
					while (hasNext()) current = iteration()

					current
				}
				is ValueArray -> {
					var current: Value = ValueNull
					for (i in range.value) extend().apply {
						setLocalVariable(variable.value, i)
						current = evaluate(action)
					}

					current
				}
				else          -> TODO()
			}
		}
		is AstWhileLoopExpression     -> {
			val condition = expr.condition
			val action = expr.action

			lateinit var env: Environment
			fun hasNext() = (unref(env.evaluate(condition)) as ValueBoolean).value
			fun next() = env.evaluate(action)

			var current: Value = ValueVoid
			while (true) {
				env = extend()
				if (!hasNext()) break
				current = next()
			}

			current
		}
		is AstCharLiteralExpression   -> ValueString(expr.value.toString())
		is AstStringLiteralExpression -> ValueString(expr.value)
		is AstNumberLiteralExpression -> ValueNumber(BigDecimal(expr.beforeComma +
				"." + (expr.afterComma ?: "0") +
				"e" + (expr.afterE ?: "0")))
		is AstPrefixOperator          -> {
			val operand = unref(evaluate(expr.operand))

			val result = with(prefixOperatorEvaluator) {
				evaluate(expr.operator, operand)
						?: throw RuntimeException("Can't evaluate operator")
			}

			result
		}
		is AstBinaryOperator          -> when (expr.operator) {
			BinaryOperator.Member        -> {
				val left = unref(evaluate(expr.left))
				val right = (expr.right as? AstReferenceExpression
						?: throw RuntimeException("Member name needed")).name.value

				when (right) {
					"hashCode" -> ValueInternalFunction(AstFunction(
							name = AstName("hashCode"),
							returnTypes = listOf(
									AstNamedTypeReference(AstName("Number"))
							))) { ValueNumber(hashCode(left)) }
					"toString" -> ValueInternalFunction(AstFunction(
							name = AstName("toString"),
							returnTypes = listOf(
									AstNamedTypeReference(AstName("String"))
							))) { ValueString(stringRepresent(left)) }
					else       -> when (left) {
						is ValueClass  -> ValueMemberReference(left, right)
						is ValueString -> when (right) {
							"contains" -> ValueInternalFunction(AstFunction(
									name = AstName("contains"),
									parameters = listOf(AstFunctionParameter(name = AstName("char"), type = AstNamedTypeReference(AstName("Char")))),
									returnTypes = listOf(AstVariantType(listOf(
											AstNamedTypeReference(AstName("Char")),
											AstNamedTypeReference(AstName("Null"))
									)))
							)) { env ->
								val str = (unref(env.getLocalVariable("char")!!) as ValueString).value
								ValueBoolean(left.value.contains(str))
							}
							"length"   -> ValueNumber(left.value.length)
							else       -> ValueNothing
						}
						else           -> ValueNothing
					}
				}
			}
			BinaryOperator.MemberDynamic -> {
				val left = unref(evaluate(expr.left))
				val right = unref(evaluate(expr.right))

				ValueDynamicMemberReference(this@Evaluator, this, left, right)
			}
			BinaryOperator.Assignment    -> {
				val left = evaluate(expr.left) as? ValueReference
						?: throw RuntimeException("Left operand is not left-value")
				val right = unref(evaluate(expr.right))

				left.value = right
				right
			}
			else                         -> {
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
