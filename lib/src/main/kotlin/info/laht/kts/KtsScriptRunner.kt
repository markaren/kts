package info.laht.kts

import java.io.File
import java.net.URLClassLoader

object KtsScriptRunner {

    init {
        System.setProperty("idea.io.use.nio2", "true")
    }

    @JvmOverloads
    fun invokeKts(scriptFile: File, timeOut: Long? = null): Any? {
        return invokeKts(scriptFile.bufferedReader().use { it.readText() }, timeOut)
    }

    @JvmOverloads
    fun invokeKts(script: String, timeOut: Long? = null): Any? {

        val repositories = KtsUtil.parseRepositories(script)

        val artifacts = KtsUtil.parseDependencies(script)
        val artifactResults = KtsUtil.resolveDependencies(artifacts, repositories)

        val classLoader = URLClassLoader(
                artifactResults.map { artifactResult ->
                    artifactResult.artifact.file!!.toURI().toURL()
                }.toTypedArray()
        )

        val classPath = artifactResults.map { artifactResult ->
            artifactResult.artifact.file
        }

        var result: Any? = null

        val thread = Thread {
            val scriptEngine = KtsScriptEngineFactory(classPath).scriptEngine
            try {
                result = scriptEngine.eval(script)
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
