package info.laht.kts

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ParseDependenciesTest {

    @Test
    fun testParseDependencies() {

        val script = """
            
            //using maven("com.google.code.gson:gson:2.8.6")
            
        """.trimIndent()

        val deps = parseDependencies(script)

        Assertions.assertEquals(1, deps.size)
        Assertions.assertEquals("2.8.6", deps.first().version)
        Assertions.assertEquals("gson", deps.first().artifactId)
        Assertions.assertEquals("com.google.code.gson", deps.first().groupId)

    }

}