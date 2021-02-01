package info.laht.kts

import java.io.File
import java.security.MessageDigest
import kotlin.script.experimental.api.*
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.host.with
import kotlin.script.experimental.jvm.baseClassLoader
import kotlin.script.experimental.jvm.compilationCache
import kotlin.script.experimental.jvm.defaultJvmScriptingHostConfiguration
import kotlin.script.experimental.jvm.jvm
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.CompiledScriptJarsCache

class KtsScriptRunner(
    private val cacheDir: File? = null
) {

    private val myHostConfiguration by lazy {
        defaultJvmScriptingHostConfiguration.with {
            jvm {
                baseClassLoader.replaceOnlyDefault(null)
                if (cacheDir != null) {
                    compilationCache(compiledScriptJarsCache(cacheDir))
                }
            }
        }
    }

    private val scriptCompilationConfiguration by lazy {
        ScriptWithMavenDepsConfiguration.with {
            hostConfiguration.update {
                myHostConfiguration
            }
        }
    }

    fun eval(scriptFile: File): Any? {
        return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), scriptCompilationConfiguration, null)
            .valueOrThrow()
            .let {
                when (val result = it.returnValue) {
                    is ResultValue.Value -> result.value
                    is ResultValue.Error -> throw result.error
                    else -> null
                }
            }
    }

    fun eval(script: String): Any? {
        return BasicJvmScriptingHost().eval(script.toScriptSource(), scriptCompilationConfiguration, null)
            .valueOrThrow()
            .let {
                when (val result = it.returnValue) {
                    is ResultValue.Value -> result.value
                    is ResultValue.Error -> throw result.error
                    else -> null
                }

            }
    }

}

internal fun compiledScriptJarsCache(cacheDir: File) =
    CompiledScriptJarsCache { script, scriptCompilationConfiguration ->
        File(cacheDir, uniqueScriptHash(script, scriptCompilationConfiguration) + ".jar")
    }

private fun uniqueScriptHash(
    script: SourceCode,
    scriptCompilationConfiguration: ScriptCompilationConfiguration
): String {
    val digestWrapper = MessageDigest.getInstance("MD5")
    digestWrapper.update(script.text.toByteArray())
    scriptCompilationConfiguration.notTransientData.entries
        .sortedBy { it.key.name }
        .forEach {
            digestWrapper.update(it.key.name.toByteArray())
            digestWrapper.update(it.value.toString().toByteArray())
        }
    return digestWrapper.digest().toHexString()
}

private fun ByteArray.toHexString(): String = joinToString("", transform = { "%02x".format(it) })

