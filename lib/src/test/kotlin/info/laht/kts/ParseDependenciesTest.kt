package info.laht.kts

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class ParseDependenciesTest {

    @Test
    fun testParseDependencies() {

        val script = """
            
            @file:DependsOn("com.google.code.gson:gson:2.8.6")
            @file:DependsOn("no.ntnu.ihb.sspgen:dsl:0.4.1", options="compile,runtime")
            
        """.trimIndent()

        val deps = KtsScriptUtil.parseDependencies(script)

        Assertions.assertEquals(2, deps.size)
        Assertions.assertEquals("2.8.6", deps.first().version)
        Assertions.assertEquals("gson", deps.first().artifactId)
        Assertions.assertEquals("com.google.code.gson", deps.first().groupId)

    }

}
