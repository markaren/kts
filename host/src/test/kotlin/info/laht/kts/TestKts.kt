package info.laht.kts

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

internal class TestKts {

    companion object {
        val cacheDir = File("build/generated")
    }

    @Test
    fun testScript() {

        val script = """
            
            @file:DependsOn("com.google.code.gson:gson:2.8.6")
            @file:DependsOn("info.laht.sspgen:dsl:0.5.1")
            
            import java.util.List
            import com.google.gson.Gson

            val gson = Gson()
            
            listOf(1,2)
            
        """.trimIndent()


        val result = KtsScriptRunner(cacheDir).eval(script)
        Assertions.assertEquals(listOf(1, 2), result)

    }

    @Test
    fun testScript2() {

        val script = """
        
        @file:DependsOn("info.laht.vico:core:0.4.2", options = arrayOf("scope=compile,runtime"))

        @file:CompilerOptions("-jvm-target", "1.8")

        import no.ntnu.ihb.vico.components.Transform
        import no.ntnu.ihb.vico.core.EngineBuilder
        import no.ntnu.ihb.vico.render.Geometry
        import no.ntnu.ihb.vico.render.mesh.BoxMesh

        EngineBuilder().build().use { engine ->

            val e1 = engine.createEntity("e1")
            e1.add<Transform>()
            e1.add(Geometry(BoxMesh()))

        }
        
    """.trimIndent()

        KtsScriptRunner(cacheDir).eval(script)

    }

}
