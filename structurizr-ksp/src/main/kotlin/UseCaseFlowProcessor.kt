package com.example

import com.example.util.FlowNode
import com.example.util.MermaidBuilder
import com.example.util.toFlowNode
import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import org.jetbrains.kotlin.cli.jvm.compiler.EnvironmentConfigFiles
import org.jetbrains.kotlin.cli.jvm.compiler.KotlinCoreEnvironment
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.config.CompilerConfiguration
import org.jetbrains.kotlin.psi.*
import java.io.File


class UseCaseFlowProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {
    var dependencies: Dependencies = Dependencies(false)
    var mermaid : String = ""

    private val generatedFiles = mutableSetOf<String>() // tracks filenames per processor instance

    override fun process(resolver: Resolver): List<KSAnnotated> {
        // val symbols = resolver.getSymbolsWithAnnotation("MyAnnotation") // or detect *UseCase

        resolver.getAllFiles().forEach { file ->
            file.declarations.filterIsInstance<KSClassDeclaration>()
                .filter { clazz ->
                    val className = clazz.simpleName.asString()
                    !className.startsWith("_KSP_") && className.endsWith("UseCase")
                }
                .forEach { useCaseClass ->
                    val fileName = "${useCaseClass.simpleName.asString()}Flow"
                    // skip if already generated in this round or previous rounds
                    if (generatedFiles.contains(fileName)) return@forEach
                    generateMermaidForUseCase(useCaseClass)
                    try {
                        val file = codeGenerator.createNewFile(
                            dependencies,
                            "docs.flows",
                            fileName,
                            "mmd"
                        )
                        file.bufferedWriter().use { it.write(mermaid) }
                        generatedFiles.add(fileName) // mark as generated
                    } catch (e: Exception) {

                    }
                }
        }

        return emptyList()
    }


    private fun generateMermaidForUseCase(useCaseClass: KSClassDeclaration) {
        dependencies = useCaseClass.toDependencies()
        mermaid = buildMermaid(useCaseClass)


    }

    private fun buildMermaid(useCaseClass: KSClassDeclaration): String {
        val method = useCaseClass.getAllFunctions()
            .firstOrNull { it.simpleName.asString() == "execute" }
            ?: return ""


        val ktFunction = method.resolveKtFunction(createEnvironment())
        logger.logging("toPsi() -> $ktFunction")

        var mermaid = ""
        if (ktFunction != null) {
            println("✅ Function body: ${ktFunction.bodyExpression?.text}")
            val flows = extractFlowFromFunction(ktFunction)
            println("flows: $flows")
            mermaid = MermaidBuilder().buildFlow(flows)
            println("Generated mermaid:\n$mermaid")
        } else {
            println("⚠️ No PSI found for ${method.simpleName.asString()}")
        }



        return mermaid
    }

    fun extractFlowFromFunction(fn: KtNamedFunction): List<FlowNode> {
        val body = fn.bodyBlockExpression ?: return emptyList()
        return body.statements.mapNotNull { stmt -> stmt.toFlowNode() }
    }


}

fun createEnvironment(): KotlinCoreEnvironment {
    val config = CompilerConfiguration()
    // Add stdlib (needed for parsing)
    config.addJvmClasspathRoots(
        listOf(File(System.getProperty("java.class.path")))
    )
    return KotlinCoreEnvironment.createForProduction(
        {}, config, EnvironmentConfigFiles.JVM_CONFIG_FILES
    )
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

/**
 * Try to resolve KSFunctionDeclaration into a KtNamedFunction using source parsing.
 */
fun KSFunctionDeclaration.resolveKtFunction(env: KotlinCoreEnvironment): KtNamedFunction? {
    val filePath = (this.location as? FileLocation)?.filePath ?: return null
    val file = File(filePath)
    if (!file.exists()) return null

    val ktFile: KtFile = KtPsiFactory(env.project).createFile(file.name, file.readText())

    return findFunctionRecursively(ktFile.declarations, this.simpleName.asString())
}


/**
 * Try to create a Dependencies object for this function's containing file.
 *
 * Falls back to empty dependencies if no file is available (synthetic/generated code).
 */
fun KSClassDeclaration.toDependencies(): Dependencies {

    val ktFile = this.containingFile

    return if (ktFile != null) {
        val filePath = ktFile.filePath
        if (filePath.isNotEmpty()) {
            Dependencies(aggregating = false, ktFile)
        } else {
            // Fallback: No real path, so return empty deps
            Dependencies(aggregating = false)
        }
    } else {
        // Safe fallback: return empty deps
        Dependencies(aggregating = false)
    }
}


class UseCaseFlowProcessorProvider : SymbolProcessorProvider {
    override fun create(
        environment: SymbolProcessorEnvironment
    ): SymbolProcessor {
        return UseCaseFlowProcessor(
            environment.codeGenerator,
            environment.logger
        )
    }
}