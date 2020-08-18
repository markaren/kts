package info.laht.kts

import java.io.File

public object KtsScriptRunner {

    init {
        System.setProperty("idea.io.use.nio2", "true")
    }

    @JvmOverloads
    public fun invokeKts(scriptFile: File, timeOut: Long? = null): Any? {
        return invokeKts(scriptFile.bufferedReader().use { it.readText() }, timeOut)
    }

    @JvmOverloads
    public fun invokeKts(script: String, timeOut: Long? = null): Any? {

        val repositories = KtsScriptUtil.parseRepositories(script)

        val artifacts = KtsScriptUtil.parseDependencies(script)
        val artifactResults = KtsScriptUtil.resolveDependencies(
            repositories.map { it.repository },
            artifacts.map { it.artifact }
        )

        val classPath = artifactResults.map { artifactResult ->
            artifactResult.artifact.file
        }

        val cleanedScript = KtsScriptUtil.removeLinesScript(
            script, repositories.map { it.lineNumber } + artifacts.map { it.lineNumber }
        )

        var result: Any? = null
        val thread = Thread {
            val scriptEngine = KtsScriptEngineFactory(classPath).scriptEngine
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
