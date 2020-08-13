package info.laht.kts

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

object Maven {

    private val LOG: Logger = LoggerFactory.getLogger(Maven::class.java)

    fun newRepositorySystem(): RepositorySystem {
        val locator: DefaultServiceLocator = MavenRepositorySystemUtils.newServiceLocator()
        locator.addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java)
        locator.addService(TransporterFactory::class.java, FileTransporterFactory::class.java)
        locator.addService(TransporterFactory::class.java, HttpTransporterFactory::class.java)
        locator.setErrorHandler(object : DefaultServiceLocator.ErrorHandler() {
            override fun serviceCreationFailed(type: Class<*>?, impl: Class<*>?, exception: Throwable) {
                LOG.error(
                    "Service creation failed for {} with implementation {}",
                    type, impl, exception
                )
            }
        })
        return locator.getService(RepositorySystem::class.java)
    }

    fun newRepositorySystemSession(system: RepositorySystem): DefaultRepositorySystemSession {

        val userDir = System.getProperty("user.home")
        val m2 = File(userDir, ".m2/repository").apply {
            if (!exists()) {
                if (!mkdirs()) {
                    throw RuntimeException("Failed to create local maven repository at: $this")
                }
            }
        }

        val session = MavenRepositorySystemUtils.newSession()
        val localRepo = LocalRepository(m2)
        session.localRepositoryManager = system.newLocalRepositoryManager(session, localRepo)

        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );
        return session
    }

    fun newRepositories(): List<RemoteRepository> {
        return listOf(
                newCentralRepository(),
                newNtnuRepository()
        )
    }

    private fun newNtnuRepository(): RemoteRepository {
        return RemoteRepository.Builder("ntnu", "default", "https://dl.bintray.com/ntnu-ihb/mvn").build()
    }

    private fun newCentralRepository(): RemoteRepository {
        return RemoteRepository.Builder("central", "default", "https://repo.maven.apache.org/maven2/").build()
    }

}
