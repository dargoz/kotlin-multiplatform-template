package com.example.util

sealed class FlowNode {
    data class IfBranch(
        val condition: String,
        val thenBranch: List<FlowNode>,
        val elseBranch: List<FlowNode>
    ) : FlowNode()

    data class ForLoop(
        val loopVar: String,        // e.g. "item"
        val collection: String,     // e.g. "items"
        val body: List<FlowNode>
    ) : FlowNode()

    data class WhileLoop(
        val condition: String,
        val body: List<FlowNode>
    ) : FlowNode()

    data class WhenBranch(val subject: String, val cases: Map<String, List<FlowNode>>) : FlowNode()

    data class ReturnNode(val expr: String?) : FlowNode()
    data class Statement(val text: String) : FlowNode()
}