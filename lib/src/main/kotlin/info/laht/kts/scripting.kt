package info.laht.kts

import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.resolution.ArtifactResult
import org.eclipse.aether.resolution.DependencyRequest
import org.eclipse.aether.util.artifact.JavaScopes
import org.eclipse.aether.util.filter.DependencyFilterUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File
import java.net.URLClassLoader
import javax.script.ScriptEngineManager

private val LOG: Logger = LoggerFactory.getLogger("scripting")

internal fun resolveDependencies(deps: List<Artifact>): List<ArtifactResult> {

    val system = Maven.newRepositorySystem()
    val session = Maven.newRepositorySystemSession(system)

    val classpathFilter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE)

    return deps.flatMap { artifact ->
        val collectRequest = CollectRequest()
        collectRequest.root = Dependency(artifact, JavaScopes.COMPILE)
        collectRequest.repositories = Maven.newRepositories()

        val dependencyRequest = DependencyRequest(collectRequest, classpathFilter)

        system.resolveDependencies(session, dependencyRequest).artifactResults
    }.also { artifactResults ->

        for (artifactResult: ArtifactResult in artifactResults) {
            LOG.trace(
                artifactResult.artifact.toString() + " resolved to "
                        + artifactResult.artifact.file
            )
        }

    }
}

internal fun parseDependencies(script: String): List<Artifact> {

    val artifacts = mutableListOf<Artifact>()
    val lines = script.split("\n")
    for (line in lines) {
        if (line.startsWith("import")) {
            break
        }
        if (line.startsWith("//using maven")) {
            val i1 = line.indexOf("(\"") + 2
            val i2 = line.indexOf("\")")
            artifacts.add(DefaultArtifact(line.substring(i1, i2)))
        }
    }

    return artifacts
}

@JvmOverloads
fun invoke(scriptFile: File, timeOut: Long? = null): Any? {
    return invoke(scriptFile.bufferedReader().use { it.readText() }, timeOut)
}

@JvmOverloads
fun invoke(script: String, timeOut: Long? = null): Any? {

    val artifacts = parseDependencies(script)
    val artifactResults = resolveDependencies(artifacts)

    val classLoader = URLClassLoader(
        artifactResults.map { artifactResult ->
            artifactResult.artifact.file!!.toURI().toURL()
        }.toTypedArray()
    )

    System.setProperty("idea.io.use.nio2", "true")

    var result: Any? = null

    val thread = Thread {
        Thread.currentThread().contextClassLoader = classLoader
        val scriptEngine = ScriptEngineManager().getEngineByExtension("kts")
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
