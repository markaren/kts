package info.laht.kts

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TestScript {

    @Test
    fun testScript() {

        val script = """
            
            //using repository("https://dl.bintray.com/ntnu-ihb/mvn")
            
            //using artifact("com.google.code.gson:gson:2.8.6")
            //using artifact("no.ntnu.ihb.sspgen:dsl:0.1.3")
            
            import java.util.List
            import com.google.gson.Gson

            val gson = Gson()
            
            listOf(1,2)

        """.trimIndent()

        val result = KtsScriptRunner.invokeKts(script)
        Assertions.assertEquals(listOf(1,2), result)

    }

}
