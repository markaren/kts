package info.laht.kts

import org.jetbrains.kotlin.cli.common.repl.*
import org.jetbrains.kotlin.script.jsr223.*
import java.io.*
import javax.script.*

/**
 * @see https://stackoverflow.com/a/44796842
 */
class KtsScriptEngineFactory(private val classpath: List<File>) : KotlinJsr223JvmScriptEngineFactoryBase() {
  override fun getScriptEngine(): ScriptEngine =
    KotlinJsr223JvmLocalScriptEngine(
      this,
      classpath,
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
