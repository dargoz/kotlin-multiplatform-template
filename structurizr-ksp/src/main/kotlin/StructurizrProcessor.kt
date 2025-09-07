package com.example

import com.example.util.MermaidBuilder
import com.example.util.collectDependencies
import com.example.util.extractFlowFromFunction
import com.example.util.resolveKtFunction
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import com.google.devtools.ksp.visitor.KSDefaultVisitor

class StructurizrProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val components = mutableMapOf<String, Component>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getAllFiles().forEach { file ->
            file.declarations.filterIsInstance<KSClassDeclaration>().forEach { clazz ->
                val className = clazz.simpleName.asString()
                if (className.startsWith("_KSP_")) return@forEach

                val type = when {
                    className.endsWith("UseCase") -> "UseCase"
                    className.endsWith("Repository") -> "Repository"
                    className.endsWith("RepositoryImpl") -> "RepositoryImpl"
                    else -> return@forEach
                }

                val component = components.getOrPut(className) { Component(className, type) }

                // constructor injection deps
                clazz.primaryConstructor?.parameters?.forEach { param ->
                    val depType = param.type.resolve().declaration.simpleName.asString()
                    component.deps.add(depType)
                }

                // scan function bodies
                clazz.getAllFunctions().forEach { function ->
                    val kotlinNamedFunction = function.resolveKtFunction(createEnvironment())
                    if (kotlinNamedFunction != null) {
                        println("✅ Function body: ${kotlinNamedFunction.bodyExpression?.text}")
                        val flows = kotlinNamedFunction.extractFlowFromFunction()
                        val deps = flows.flatMap { it.collectDependencies() }
                        deps.forEach { dep ->
                            when (dep.type) {
                                "RemoteDataSource" -> component.deps.add("RemoteDataSource:${dep.detail}")
                                "LocalDataSource" -> component.deps.add("LocalDataSource:${dep.detail}")
                            }
                        }
                        println("flows: $flows")

                    } else {
                        println("⚠️ No PSI found for ${function.simpleName.asString()}")
                    }
                }

            }
        }

        return emptyList()
    }

    override fun finish() {
        if (components.isEmpty()) return

        // Add special components if referenced
        if (components.values.any { it.deps.any { c -> c == "RemoteDataSource" } }) {
            components.putIfAbsent("ExternalAPI", Component("ExternalAPI", "API"))
        }
        if (components.values.any { it.deps.any { c -> c == "LocalDataSource" } }) {
            components.putIfAbsent("Database", Component("Database", "Database"))
        }

        val file = codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            packageName = "",
            fileName = "structurizr-workspace",
            extensionName = "dsl"
        )

        file.bufferedWriter().use { out -> out.write(generateDslContent()) }
        logger.info("✅ Structurizr DSL generated with ${components.size} components")
    }

    private fun generateDslContent(): String = buildString {
        appendLine("workspace \"Kotlin App\" {")
        appendLine("  model {")
        appendLine("    user = person \"User\"")
        appendLine("    system = softwareSystem \"MyKotlinApp\" {")
        appendLine("      app = container \"Application\" {")
        components.values.forEach { comp ->
            val id = comp.dslId()
            appendLine("        $id = component \"${comp.name}\" \"Auto-detected\" \"Kotlin\" {")
            appendLine("          tags \"${comp.type}\"")
            appendLine("        }")
        }
        appendLine("      }")
        appendLine("    }")
        appendLine("    user -> system \"Uses\"")

        components.values.forEach { comp ->
            val sourceId = comp.dslId()
            // constructor deps (generic "Uses")
            comp.deps.forEach { dep ->
                if (dep.startsWith("RemoteDataSource:")) {
                    val url = dep.removePrefix("RemoteDataSource:")
                    appendLine("    $sourceId -> remoteApi \"Calls $url\"")
                } else if (dep.startsWith("LocalDataSource:")) {
                    val op = dep.removePrefix("LocalDataSource:")
                    appendLine("    $sourceId -> localDb \"Executes $op\"")
                } else if (components.containsKey(dep)) {
                    val targetId = dep.replaceFirstChar { it.lowercaseChar() }
                    appendLine("    $sourceId -> $targetId \"Uses\"")
                }
            }
            // function-level calls with refined labels
            comp.calls.forEach { call ->
                val targetId = call.target.replaceFirstChar { it.lowercaseChar() }
                appendLine("    $sourceId -> $targetId \"${call.label}\"")
            }
        }

        appendLine("  }")
        appendLine("  views {")
        appendLine("    container system { include * autolayout lr }")
        appendLine("    component app { include * autolayout lr }")
        appendLine("  }")
        appendLine("}")
    }

    data class Component(
        val name: String,
        val type: String,
        val deps: MutableSet<String> = mutableSetOf(),
        val calls: MutableList<Call> = mutableListOf()
    ) {
        fun dslId() = name.replaceFirstChar { it.lowercaseChar() }
    }

    data class Call(val target: String, val label: String)


}

class StructurizrProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return StructurizrProcessor(environment.codeGenerator, environment.logger)
    }
}
