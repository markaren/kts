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
            
            @file:Repository("https://dl.bintray.com/ntnu-ihb/mvn")
            
            @file:DependsOn("com.google.code.gson:gson:2.8.6")
            @file:DependsOn("no.ntnu.ihb.sspgen:dsl:0.4.1")
            
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
        
        @file:Repository("https://dl.bintray.com/laht/mvn")
        @file:Repository("https://dl.bintray.com/ntnu-ihb/mvn")

        @file:DependsOn("no.ntnu.ihb.vico:core:0.3.3", options = arrayOf("scope=compile,runtime"))
        @file:DependsOn("no.ntnu.ihb.vico:threekt-render:0.3.3", options = arrayOf("scope=compile,runtime"))

        @file:CompilerOptions("-jvm-target", "1.8")

        import info.laht.krender.threekt.ThreektRenderer
        import no.ntnu.ihb.vico.components.Transform
        import no.ntnu.ihb.vico.core.EngineBuilder
        import no.ntnu.ihb.vico.render.Geometry
        import no.ntnu.ihb.vico.render.GeometryRenderer
        import no.ntnu.ihb.vico.render.mesh.BoxMesh

        val renderer = ThreektRenderer()
        EngineBuilder().renderer(renderer).build().use { engine ->

            val e1 = engine.createEntity("e1")
            e1.add<Transform>()
            e1.add(Geometry(BoxMesh()))

            engine.addSystem(GeometryRenderer())

            engine.runner.startAndWait()

        }
        
    """.trimIndent()

        KtsScriptRunner(cacheDir).eval(script)

    }

}
