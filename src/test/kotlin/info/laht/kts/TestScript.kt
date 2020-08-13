package info.laht.kts

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

internal class TestScript {

    @Test
    fun testScript() {

        val script = """
            
            //using maven("com.google.code.gson:gson:2.8.6")
            
            import java.util.List
            import com.google.gson.Gson

            val gson = Gson()
            
            listOf(1,2)

        """.trimIndent()

        val result = invoke(script)
        Assertions.assertEquals(listOf(1,2), result)

    }

}
