package info.laht.kts

import java.io.File
import javax.script.ScriptEngineManager

private val repositories = mutableListOf(
    "https://repo1.maven.org/maven2"
)

private val scriptEngine
    get() = ScriptEngineManager().getEngineByExtension("kts")

private fun setupScriptingEnvironment() {
    System.setProperty("idea.io.use.nio2", "true")
}

private fun resolveDependencies(script: String) {

}

private fun parseDependencies(script: String): List<Dependency> {

    val lines = script.split("\n")
    for (line in lines) {
        if (line.startsWith("//using maven")) {
            val i1 = line.indexOf("(\"")
            val i2 = line.indexOf("\")")

            val dep = line.substring(i1, i2).split(":")
        }
    }

    TODO()
}

fun invoke(scriptFile: File) {
    invoke(scriptFile.bufferedReader().use { it.readText() })
}

fun invoke(script: String): Any? {

    setupScriptingEnvironment()
    return try {
        scriptEngine.eval(script)
    } catch (ex: Exception) {
        ex.printStackTrace()
        null
    }

}

data class Dependency(
    val group: String,
    val name: String,
    val version: String
)
