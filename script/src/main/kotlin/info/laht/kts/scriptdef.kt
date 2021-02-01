package info.laht.kts

import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.mainKts.CompilerOptions
import kotlin.script.experimental.api.*
import kotlin.script.experimental.dependencies.*
import kotlin.script.experimental.dependencies.maven.MavenDependenciesResolver
import kotlin.script.experimental.jvm.JvmDependency
import kotlin.script.experimental.jvm.dependenciesFromCurrentContext
import kotlin.script.experimental.jvm.jvm

object ScriptWithMavenDepsConfiguration : ScriptCompilationConfiguration(
    {
        defaultImports(DependsOn::class, Repository::class, CompilerOptions::class)
        jvm {
            dependenciesFromCurrentContext(
                "script", // script library jar name
                "kotlin-main-kts", // CompilerOptions
                "kotlin-scripting-dependencies" // DependsOn annotation is taken from this jar
            )
        }
        refineConfiguration {
            onAnnotations(DependsOn::class, Repository::class, handler = ::configureMavenDepsOnAnnotations)
        }
    }
)

private val resolver =
    CompoundDependenciesResolver(FileSystemDependenciesResolver(), MavenDependenciesResolver().apply {
        addRepository("https://repo.maven.apache.org/maven2/")
    })

private fun configureMavenDepsOnAnnotations(context: ScriptConfigurationRefinementContext): ResultWithDiagnostics<ScriptCompilationConfiguration> {
    val annotations = context.collectedData?.get(ScriptCollectedData.collectedAnnotations)?.takeIf { it.isNotEmpty() }
        ?: return context.compilationConfiguration.asSuccess()
    return runBlocking {
        resolver.resolveFromScriptSourceAnnotations(annotations)
    }.onSuccess {
        context.compilationConfiguration.with {
            dependencies.append(JvmDependency(it))
        }.asSuccess()
    }
}
