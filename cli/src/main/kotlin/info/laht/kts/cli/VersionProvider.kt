package info.laht.kts.cli

import info.laht.kts.kts
import picocli.CommandLine

class VersionProvider : CommandLine.IVersionProvider {

    override fun getVersion(): Array<String> {
        return arrayOf(kts.version)
    }
}
