package com.example

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*

class StructurizrProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    // Collect components across all rounds
    private val components = mutableMapOf<String, Component>()

    override fun process(resolver: Resolver): List<KSAnnotated> {
        resolver.getAllFiles().forEach { file ->
            file.declarations.filterIsInstance<KSClassDeclaration>().filter { clazz ->
                val className = clazz.simpleName.asString()
                !className.startsWith("_KSP_")
                        && (className.endsWith("UseCase") || className.endsWith("Repository"))
            }.forEach { clazz ->
                val className = clazz.simpleName.asString()
                val type = when {
                    className.endsWith("UseCase") -> "UseCase"
                    className.endsWith("Repository") -> "Repository"
                    else -> "Other"
                }

                val component = components.getOrPut(className) { Component(className, type) }

                // constructor injection deps
                clazz.primaryConstructor?.parameters?.forEach { param ->
                    val depType = param.type.resolve().declaration.simpleName.asString()
                    component.deps.add(depType)
                }
            }
        }

        return emptyList() // no deferred symbols
    }

    override fun finish() {
        if (components.isEmpty()) return

        // Generate file only once
        val file = codeGenerator.createNewFile(
            Dependencies.ALL_FILES,
            packageName = "",
            fileName = "structurizr-workspace",
            extensionName = "dsl"
        )

        file.bufferedWriter().use { out ->
            out.write(generateDslContent())
        }

        logger.info("✅ Structurizr DSL generated with ${components.size} components")
    }

    private fun generateDslContent(): String = buildString {
        appendLine("workspace \"Kotlin App\" {")
        appendLine("  model {")
        appendLine("    user = person \"User\"")
        appendLine("    system = softwareSystem \"MyKotlinApp\" {")
        appendLine("      app = container \"Application\" {")
        components.values.forEach { comp ->
            val id = comp.name.replaceFirstChar { it.lowercaseChar() } // e.g. FeatureUseCase -> featureUseCase
            appendLine("        $id = component \"${comp.name}\" \"Auto-detected component\" \"Kotlin\" {")
            appendLine("          tags \"${comp.type}\"")
            appendLine("        }")
        }

        appendLine("      }")
        appendLine("    }")
        appendLine("    user -> system \"Uses\"")

        components.values.forEach { comp ->
            val sourceId = comp.name.replaceFirstChar { it.lowercaseChar() }
            comp.deps.forEach { dep ->
                if (components.containsKey(dep)) {
                    val targetId = dep.replaceFirstChar { it.lowercaseChar() }
                    appendLine("    $sourceId -> $targetId \"Uses\"")
                }
            }
        }

        appendLine("  }")
        appendLine("  views {")
        appendLine("    container system {")
        appendLine("      include *")
        appendLine("      autolayout lr")
        appendLine("    }")
        appendLine("    component app {")
        appendLine("      include *")
        appendLine("      autolayout lr")
        appendLine("    }")
        appendLine("  }")
        appendLine("}")
    }

    data class Component(
        val name: String,
        val type: String,
        val deps: MutableSet<String> = mutableSetOf()
    )
}

class StructurizrProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return StructurizrProcessor(environment.codeGenerator, environment.logger)
    }
}