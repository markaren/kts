package info.laht.kts

import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactResult
import org.eclipse.aether.resolution.DependencyRequest
import org.eclipse.aether.util.artifact.JavaScopes
import org.eclipse.aether.util.filter.DependencyFilterUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory

internal object KtsScriptUtil {

    private val LOG: Logger = LoggerFactory.getLogger(KtsScriptUtil::class.java)

    internal fun cleanScript(script: String): String {

        val lines = script.split("\n").mapIndexedNotNull { index, line ->
            if (line.startsWith("@file:")) index else null
        }

        val scriptLines = script.split("\n").toMutableList()
        for ((index, lineNumber) in lines.withIndex()) {
            scriptLines.removeAt(lineNumber-index)
        }
        return scriptLines.joinToString("\n")
    }

    internal fun resolveDependencies(repositories: List<RemoteRepository>, deps: Iterable<Artifact>): List<ArtifactResult> {

        val system = KtsMavenHandler.newRepositorySystem()
        val session = KtsMavenHandler.newRepositorySystemSession(system)

        val classpathFilter = DependencyFilterUtils.classpathFilter(JavaScopes.RUNTIME)

        return deps.flatMap { artifact ->
            val collectRequest = CollectRequest()
            collectRequest.root = Dependency(artifact, JavaScopes.RUNTIME)
            collectRequest.repositories = KtsMavenHandler.defaultRepositories() + repositories

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
            if (line.startsWith("@file:DependsOn")) {
                val i1 = line.indexOf("(\"") + 2
                val i2 = line.indexOf("\")")
                val artifact = DefaultArtifact(line.substring(i1, i2))
                artifacts.add(artifact)
            }
        }

        return artifacts
    }

    internal fun parseRepositories(script: String): List<RemoteRepository> {
        val repositories = mutableListOf<RemoteRepository>()
        val lines = script.split("\n")
        for (line in lines) {
            if (line.startsWith("import")) {
                break
            }
            if (line.startsWith("@file:Repository")) {
                val i1 = line.indexOf("(\"") + 2
                val i2 = line.indexOf("\")")
                val repo = line.substring(i1, i2)
                val repository = RemoteRepository.Builder(repo, "default", repo).build()
                repositories.add(repository)
            }
        }

        return repositories
    }

}
