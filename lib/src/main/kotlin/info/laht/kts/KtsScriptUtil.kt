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

    internal fun removeLinesScript(script: String, lines: List<Int>): String {
        val scriptLines = script.split("\n").toMutableList()
        for ((index, lineNumber) in lines.withIndex()) {
            scriptLines.removeAt(lineNumber-index)
        }
        return scriptLines.joinToString("\n")
    }

    internal fun resolveDependencies(repositories: List<RemoteRepository>, deps: Iterable<Artifact>): List<ArtifactResult> {

        val system = KtsMavenHandler.newRepositorySystem()
        val session = KtsMavenHandler.newRepositorySystemSession(system)

        val classpathFilter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE)

        return deps.flatMap { artifact ->
            val collectRequest = CollectRequest()
            collectRequest.root = Dependency(artifact, JavaScopes.COMPILE)
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

    internal fun parseDependencies(script: String): List<ParsedArtifact> {
        val artifacts = mutableListOf<ParsedArtifact>()
        val lines = script.split("\n")
        for ((index, line) in lines.withIndex()) {
            if (line.startsWith("import")) {
                break
            }
            if (line.startsWith("@file:DependsOn")) {
                val i1 = line.indexOf("(\"") + 2
                val i2 = line.indexOf("\")")
                val artifact = DefaultArtifact(line.substring(i1, i2))
                artifacts.add(ParsedArtifact(index, artifact))
            }
        }

        return artifacts
    }

    internal fun parseRepositories(script: String): List<ParsedRepository> {
        val repositories = mutableListOf<ParsedRepository>()
        val lines = script.split("\n")
        for ((index, line) in lines.withIndex()) {
            if (line.startsWith("import")) {
                break
            }
            if (line.startsWith("@file:Repository")) {
                val i1 = line.indexOf("(\"") + 2
                val i2 = line.indexOf("\")")
                val repo = line.substring(i1, i2)
                val repository = RemoteRepository.Builder(repo, "default", repo).build()
                repositories.add(ParsedRepository(index, repository))
            }
        }

        return repositories
    }

}

internal data class ParsedRepository(
    val lineNumber: Int,
    val repository: RemoteRepository
)

internal data class ParsedArtifact(
    val lineNumber: Int,
    val artifact: Artifact
)
