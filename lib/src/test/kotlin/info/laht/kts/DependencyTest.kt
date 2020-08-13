package info.laht.kts

import org.eclipse.aether.artifact.Artifact
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.resolution.ArtifactResult
import org.eclipse.aether.resolution.DependencyRequest
import org.eclipse.aether.util.artifact.JavaScopes
import org.eclipse.aether.util.filter.DependencyFilterUtils
import org.junit.jupiter.api.Test

//https://stackoverflow.com/questions/40813062/maven-get-all-dependencies-programmatically
//https://stackoverflow.com/questions/39638138/find-all-direct-dependencies-of-an-artifact-on-maven-central/39641359#39641359
internal class DependencyTest {

    @Test
    fun test() {

        val system = Maven.newRepositorySystem()
        val session = Maven.newRepositorySystemSession(system)

        val artifact: Artifact = DefaultArtifact("no.ntnu.ihb.sspgen:dsl:0.1.3")

        val classpathFilter = DependencyFilterUtils.classpathFilter(JavaScopes.COMPILE)

        val collectRequest = CollectRequest()
        collectRequest.root = Dependency(artifact, JavaScopes.COMPILE)
        collectRequest.repositories = Maven.newRepositories()

        val dependencyRequest = DependencyRequest(collectRequest, classpathFilter)

        val artifactResults = system.resolveDependencies(session, dependencyRequest).artifactResults

        for (artifactResult: ArtifactResult in artifactResults) {
            println(
                artifactResult.artifact.toString() + " resolved to "
                        + artifactResult.artifact.file
            )
        }

    }

}
