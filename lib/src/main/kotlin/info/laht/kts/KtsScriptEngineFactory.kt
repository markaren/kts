package info.laht.kts

import org.jetbrains.kotlin.cli.common.repl.*
import org.jetbrains.kotlin.script.jsr223.*
import java.io.*
import java.net.*
import javax.script.*
import kotlin.script.experimental.jvm.util.*

/**
 * @see "https://stackoverflow.com/a/44796842"
 */
class KtsScriptEngineFactory(private val classpath: List<File>) : KotlinJsr223JvmScriptEngineFactoryBase() {
  override fun getScriptEngine(): ScriptEngine {
    val urlCL = URLClassLoader(arrayOf(), this::class.java.classLoader)
    return KotlinJsr223JvmLocalScriptEngine(
      this,
      classpath + urlCL.classPathFromTypicalResourceUrls(),
      KotlinStandardJsr223ScriptTemplate::class.qualifiedName!!,
      { ctx, types ->
        ScriptArgsWithTypes(
          arrayOf(ctx.getBindings(ScriptContext.ENGINE_SCOPE)),
          types ?: emptyArray()
        )
      },
      arrayOf(Bindings::class)
    )
  }
}
