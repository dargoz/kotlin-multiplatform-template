package com.example.util

class MermaidBuilder {
    private var counter = 0
    private fun nextId(prefix: String) = "${prefix}_${counter++}"

    private fun esc(s: String): String =
        s.replace("\"", "")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\n", " ")
            .replace("\r", "")

    fun buildFlow(nodes: List<FlowNode>): String {
        val sb = StringBuilder("flowchart TD\n")
        renderSequence(nodes, sb, null)
        return sb.toString()
    }

    private fun renderSequence(
        nodes: List<FlowNode>,
        sb: StringBuilder,
        from: String?
    ): String? {
        var prev = from
        for (node in nodes) {
            val id = renderNode(node, sb, prev)
            prev = id
        }
        return prev // null if nodes is empty, safe
    }

    private fun endsWithReturn(node: FlowNode?): Boolean {
        if (node == null) return false

        return when (node) {
            is FlowNode.ReturnNode -> true
            is FlowNode.IfBranch -> {
                val thenReturn = node.thenBranch.lastOrNull()?.let { endsWithReturn(it) } ?: false
                val elseReturn = node.elseBranch.lastOrNull()?.let { endsWithReturn(it) } ?: false
                thenReturn && elseReturn
            }
            is FlowNode.ForLoop, is FlowNode.WhileLoop -> false
            is FlowNode.Statement -> false
            else -> false
        }
    }

    private fun renderNode(node: FlowNode, sb: StringBuilder, from: String?): String {
        return when (node) {
            is FlowNode.IfBranch -> {
                val id = nextId("if")
                sb.appendLine("$id{\"${esc(node.condition)}\"}")
                if (from != null) sb.appendLine("$from --> $id")

                val thenEnd = renderSequence(node.thenBranch, sb, id)
                val elseEnd = renderSequence(node.elseBranch, sb, id)

                // Only create merge if not both branches end with return
                val thenEnds = thenEnd?.let { node.thenBranch.lastOrNull()?.let { endsWithReturn(it) } } ?: false
                val elseEnds = elseEnd?.let { node.elseBranch.lastOrNull()?.let { endsWithReturn(it) } } ?: false

                val mergeId = if (!thenEnds || !elseEnds) nextId("merge") else null

                if (thenEnd != null) sb.appendLine("$thenEnd --> ${mergeId ?: thenEnd}")
                if (elseEnd != null) sb.appendLine("$elseEnd --> ${mergeId ?: elseEnd}")

                if (mergeId != null) sb.appendLine("$mergeId([\"merge\"])")

                mergeId ?: id
            }

            is FlowNode.ReturnNode -> {
                val id = nextId("ret")
                val label = node.expr?.let { "return ${esc(it)}" } ?: "return"
                sb.appendLine("$id([\"$label\"])")
                if (from != null) sb.appendLine("$from --> $id")
                id
            }

            is FlowNode.Statement -> {
                val id = nextId("stmt")
                sb.appendLine("$id([\"${esc(node.text)}\"])")
                if (from != null) sb.appendLine("$from --> $id")
                id
            }

            is FlowNode.ForLoop -> {
                val id = nextId("for")
                sb.appendLine("$id{\"for (${esc(node.loopVar)} in ${esc(node.collection)})\"}")
                if (from != null) sb.appendLine("$from --> $id")

                val bodyEnd = renderSequence(node.body, sb, id)
                if (bodyEnd != null) sb.appendLine("$bodyEnd --> $id")

                val exitId = nextId("exit")
                sb.appendLine("$exitId([\"exit loop\"])")
                sb.appendLine("$id -->|false| $exitId")
                exitId
            }

            is FlowNode.WhileLoop -> {
                val id = nextId("while")
                sb.appendLine("$id{\"while (${esc(node.condition)})\"}")
                if (from != null) sb.appendLine("$from --> $id")

                val bodyEnd = renderSequence(node.body, sb, id)
                if (bodyEnd != null) sb.appendLine("$bodyEnd --> $id")

                val exitId = nextId("exit")
                sb.appendLine("$exitId([\"exit loop\"])")
                sb.appendLine("$id -->|false| $exitId")
                exitId
            }

            else -> {
                val id = nextId("n")
                if (from != null) sb.appendLine("$from --> $id")
                id
            }
        }
    }
}


