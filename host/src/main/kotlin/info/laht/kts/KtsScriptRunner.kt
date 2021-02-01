package info.laht.kts

import java.io.File
import kotlin.script.experimental.api.ResultValue
import kotlin.script.experimental.api.valueOrThrow
import kotlin.script.experimental.host.toScriptSource
import kotlin.script.experimental.jvmhost.BasicJvmScriptingHost
import kotlin.script.experimental.jvmhost.createJvmCompilationConfigurationFromTemplate

object KtsScriptRunner {

    private val compilationConfiguration by lazy {
        createJvmCompilationConfigurationFromTemplate<ScriptWithMavenDeps>()
    }

    fun eval(scriptFile: File): Any? {
        return BasicJvmScriptingHost().eval(scriptFile.toScriptSource(), compilationConfiguration, null).valueOrThrow()
            .let {
                when (val result = it.returnValue) {
                    is ResultValue.Value -> result.value
                    is ResultValue.Error -> throw result.error
                    else -> null
                }
            }
    }

    fun eval(script: String): Any? {
        return BasicJvmScriptingHost().eval(script.toScriptSource(), compilationConfiguration, null).valueOrThrow()
            .let {
                when (val result = it.returnValue) {
                    is ResultValue.Value -> result.value
                    is ResultValue.Error -> throw result.error
                    else -> null
                }

            }
    }

}
