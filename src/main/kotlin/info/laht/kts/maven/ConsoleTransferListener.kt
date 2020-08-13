package info.laht.kts.maven

import org.eclipse.aether.transfer.AbstractTransferListener
import org.eclipse.aether.transfer.MetadataNotFoundException
import org.eclipse.aether.transfer.TransferEvent
import org.eclipse.aether.transfer.TransferResource
import java.io.PrintStream
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.min

/**
 * A simplistic transfer listener that logs uploads/downloads to the console.
 */
class ConsoleTransferListener @JvmOverloads constructor(
    out: PrintStream? = null
) : AbstractTransferListener() {

    private val out: PrintStream = out ?: System.out
    private val downloads: MutableMap<TransferResource, Long> = ConcurrentHashMap()
    private var lastLength = 0

    override fun transferInitiated(event: TransferEvent) {
        val message = if (event.requestType == TransferEvent.RequestType.PUT) "Uploading" else "Downloading"
        out.println(message + ": " + event.resource.repositoryUrl + event.resource.resourceName)
    }

    override fun transferProgressed(event: TransferEvent) {
        val resource = event.resource
        downloads[resource] = event.transferredBytes
        val buffer = StringBuilder(64)
        for ((key, complete) in downloads) {
            val total = key.contentLength
            buffer.append(getStatus(complete, total)).append("  ")
        }
        val pad = lastLength - buffer.length
        lastLength = buffer.length
        pad(buffer, pad)
        buffer.append('\r')
        out.print(buffer)
    }

    private fun getStatus(complete: Long, total: Long): String {
        return when {
            total >= 1024 -> {
                toKB(complete).toString() + "/" + toKB(total) + " KB "
            }
            total >= 0 -> {
                "$complete/$total B "
            }
            complete >= 1024 -> {
                toKB(complete).toString() + " KB "
            }
            else -> {
                "$complete B "
            }
        }
    }

    private fun pad(buffer: StringBuilder, spaces: Int) {
        @Suppress("NAME_SHADOWING")
        var spaces = spaces
        val block = "                                        "
        while (spaces > 0) {
            val n = min(spaces, block.length)
            buffer.append(block, 0, n)
            spaces -= n
        }
    }

    override fun transferSucceeded(event: TransferEvent) {
        transferCompleted(event)
        val resource = event.resource
        val contentLength = event.transferredBytes
        if (contentLength >= 0) {
            val type = if (event.requestType == TransferEvent.RequestType.PUT) "Uploaded" else "Downloaded"
            val len = if (contentLength >= 1024) toKB(contentLength).toString() + " KB" else "$contentLength B"
            var throughput = ""
            val duration = System.currentTimeMillis() - resource.transferStartTime
            if (duration > 0) {
                val bytes = contentLength - resource.resumeOffset
                val format = DecimalFormat("0.0", DecimalFormatSymbols(Locale.ENGLISH))
                val kbPerSec = bytes / 1024.0 / (duration / 1000.0)
                throughput = " at " + format.format(kbPerSec) + " KB/sec"
            }
            out.println(
                type + ": " + resource.repositoryUrl + resource.resourceName + " (" + len + throughput + ")"
            )
        }
    }

    override fun transferFailed(event: TransferEvent) {
        transferCompleted(event)
        if (event.exception !is MetadataNotFoundException) {
            event.exception.printStackTrace(out)
        }
    }

    private fun transferCompleted(event: TransferEvent) {
        downloads.remove(event.resource)
        val buffer = StringBuilder(64)
        pad(buffer, lastLength)
        buffer.append('\r')
        out.print(buffer)
    }

    override fun transferCorrupted(event: TransferEvent) {
        event.exception.printStackTrace(out)
    }

    private fun toKB(bytes: Long): Long {
        return (bytes + 1023) / 1024
    }

}
