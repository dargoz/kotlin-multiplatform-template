package com.example.util

import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.psi.*
import java.io.File

internal fun KtExpression?.asFlowNodes(): List<FlowNode> = when (this) {
    null -> emptyList()
    is KtBlockExpression -> this.statements.mapNotNull { it.toFlowNode() }
    else -> listOfNotNull(this.toFlowNode())
}

internal fun KtExpression.toFlowNode(): FlowNode? = when (this) {
    is KtIfExpression -> FlowNode.IfBranch(
        condition = condition?.text ?: "<unknown>",
        thenBranch = then.asFlowNodes(),
        elseBranch = `else`.asFlowNodes()
    )

    is KtForExpression -> FlowNode.ForLoop(
        loopVar = loopParameter?.text ?: "<iter>",
        collection = loopRange?.text ?: "<?>"   ,
        body = body.asFlowNodes()
    )

    is KtWhileExpression -> FlowNode.WhileLoop(
        condition = condition?.text ?: "<condition>",
        body = body.asFlowNodes()
    )

    is KtWhenExpression -> FlowNode.WhenBranch(
        subject = subjectExpression?.text ?: "<subject>",
        cases = entries.associate { entry ->
            val cond = entry.conditions.joinToString(" || ") { it.text }
            cond to entry.expression.asFlowNodes()
        }
    )

    is KtReturnExpression -> FlowNode.ReturnNode(returnedExpression?.text)
    is KtCallExpression -> {
        println(" callee : ${calleeExpression?.text}")
        val parentDot = this.parent as? KtDotQualifiedExpression
        val receiver = parentDot?.receiverExpression?.text // e.g. "remoteDataSource"
        println(" receiver : $receiver")
        val callee = calleeExpression?.text               // e.g. "get"

        if (receiver == "remoteDataSource" && callee == "get") {
            val urlArg = valueArguments.firstOrNull()?.getArgumentExpression()?.text?.trim('"')
            FlowNode.ApiCall(urlArg ?: "unknown")
        } else if (receiver == "localDataSource") {
            FlowNode.DbCall(callee ?: "unknown")
        } else {
            FlowNode.Statement(text)
        }
    }
    is KtProperty -> {
        println("KtExpression name: ${this.name}")
        println("KtExpression text: ${this.text}")
        println("KtExpression node: ${this.node}")
        val init = initializer
        println("---------------")
        println("KtExpression init text: ${init?.text}")
        println("KtExpression init node: ${init?.node}")
        when (init) {
            is KtCallExpression -> init.toFlowNode()
            is KtDotQualifiedExpression -> {
                val call = init.selectorExpression
                println("-------------------")
                println("KtExpression call text: ${call?.text}")
                println("KtExpression call node: ${call?.node}")
                call?.toFlowNode() ?: FlowNode.Statement(this.text)
            }
            else -> FlowNode.Statement(this.text)
        }
    }


    else -> {
        FlowNode.Statement(this.text)
    }
}

/**
 * Try to resolve KSFunctionDeclaration into a KtNamedFunction using source parsing.
 */
internal fun KSFunctionDeclaration.resolveKtFunction(env: KotlinCoreEnvironment): KtNamedFunction? {
    val filePath = (this.location as? FileLocation)?.filePath ?: return null
    val file = File(filePath)
    if (!file.exists()) return null

    val ktFile: KtFile = KtPsiFactory(env.project).createFile(file.name, file.readText())

    return findFunctionRecursively(ktFile.declarations, this.simpleName.asString())
}

/**
 * Recursively search for a function by name in a PSI declaration.
 */
private fun findFunctionRecursively(
    declarations: List<KtDeclaration>,
    targetName: String
): KtNamedFunction? {
    declarations.forEach { decl ->
        when (decl) {
            is KtNamedFunction -> {
                if (decl.name == targetName) return decl
            }
            is KtClassOrObject -> {
                val nested = findFunctionRecursively(decl.declarations, targetName)
                if (nested != null) return nested
            }
        }
    }
    return null
}

fun KtNamedFunction.extractFlowFromFunction(): List<FlowNode> {
    val body = this.bodyBlockExpression ?: return emptyList()
    return body.statements.mapNotNull { stmt -> stmt.toFlowNode() }
}

fun FlowNode.collectDependencies(): List<Dependency> = when (this) {
    is FlowNode.IfBranch -> thenBranch.flatMap { it.collectDependencies() } +
            elseBranch.flatMap { it.collectDependencies() }

    is FlowNode.ForLoop -> body.flatMap { it.collectDependencies() }
    is FlowNode.WhileLoop -> body.flatMap { it.collectDependencies() }
    is FlowNode.WhenBranch -> cases.values.flatten().flatMap { it.collectDependencies() }

    is FlowNode.ApiCall -> listOf(Dependency("RemoteDataSource", url))
    is FlowNode.DbCall -> listOf(Dependency("LocalDataSource", operation))

    else -> emptyList()
}

