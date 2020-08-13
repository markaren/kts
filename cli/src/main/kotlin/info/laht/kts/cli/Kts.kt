package info.laht.kts.cli

import info.laht.kts.invoke
import picocli.CommandLine
import java.io.File

class Kts: Runnable {

    @CommandLine.Parameters(
        arity = "1",
        paramLabel = "SCRIPT",
        description = ["Path to script"]
    )
    private lateinit var scriptFile: File

    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["Display a help message"])
    private var helpRequested: Boolean = false

    override fun run() {

        invoke(scriptFile)

    }

    companion object {

       @JvmStatic
       fun main(args: Array<out String>) {
           CommandLine(Kts()).execute(*args)
       }

   }

}

