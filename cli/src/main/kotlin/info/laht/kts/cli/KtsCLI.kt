package info.laht.kts.cli

import info.laht.kts.KtsScriptRunner
import info.laht.kts.kts
import picocli.CommandLine
import java.io.File

@CommandLine.Command(versionProvider = VersionProvider::class)
class KtsCLI: Runnable {

    @CommandLine.Parameters(
        arity = "1",
        paramLabel = "SCRIPT",
        description = ["Path to script"]
    )
    private lateinit var scriptFile: File

    @CommandLine.Option(names = ["-h", "--help"], usageHelp = true, description = ["Display a help message"])
    private var helpRequested: Boolean = false

    @CommandLine.Option(
    names = ["-v", "--version"],
    versionHelp = true,
    description = ["Print the version of this application."]
    )
    private var showVersion = false

    override fun run() {

        KtsScriptRunner.eval(scriptFile)?.also { result ->
            println(result) // print non-null invocation results
        }

    }

    companion object {

       @JvmStatic
       fun main(args: Array<out String>) {
           CommandLine(KtsCLI()).execute(*args)
       }

   }

}

class VersionProvider : CommandLine.IVersionProvider {

    override fun getVersion(): Array<String> {
        return arrayOf(kts.version)
    }
}

