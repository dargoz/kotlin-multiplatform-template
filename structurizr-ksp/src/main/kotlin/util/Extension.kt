package com.example.util

import org.jetbrains.kotlin.psi.*

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

    else -> FlowNode.Statement(this.text)
}

