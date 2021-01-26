package info.laht.kts

import java.io.File
import java.net.URLClassLoader
import javax.script.ScriptEngineManager

public object KtsScriptRunner {

    init {
        System.setProperty("idea.io.use.nio2", "true")
    }

    @JvmOverloads
    public fun invokeKts(scriptFile: File, timeOut: Long? = null): Any? {
        require(scriptFile.exists()) {
            "No such file: ${scriptFile.absolutePath}"
        }
        return invokeKts(scriptFile.bufferedReader().use { it.readText() }, timeOut)
    }

    @JvmOverloads
    public fun invokeKts(script: String, timeOut: Long? = null): Any? {

        val repositories = KtsScriptUtil.parseRepositories(script)

        val artifacts = KtsScriptUtil.parseDependencies(script)
        val artifactResults = KtsScriptUtil.resolveDependencies(
            repositories, artifacts
        )

        val classPath = artifactResults.map { artifactResult ->
            artifactResult.artifact.file
        }

        val cleanedScript = KtsScriptUtil.cleanScript(script)

        var result: Any? = null
        val thread = Thread {
            Thread.currentThread().contextClassLoader = URLClassLoader(
                classPath.map { cl ->
                    cl.toURI().toURL()
                }.toTypedArray()
            )
            val scriptEngine = ScriptEngineManager().getEngineByExtension("main.kts")!!
            try {
                result = scriptEngine.eval(cleanedScript)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        thread.start()

        if (timeOut == null) {
            thread.join()
        } else {
            thread.join(timeOut)
        }

        return result

    }

}
