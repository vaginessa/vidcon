package io.github.jsixface.api

import io.github.jsixface.common.Conversion
import io.github.jsixface.common.MediaTrack
import io.github.jsixface.common.VideoFile
import io.github.jsixface.logger
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import java.io.File
import java.io.InputStream
import java.io.UncheckedIOException
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ConversionApi {
    private val logger = logger()
    private val currentJobs = mutableMapOf<String, Job>()
    private val currentProgress = mutableMapOf<String, MutableStateFlow<Int>>()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        if (ConversionOutLocation.isDirectory.not()) {
            ConversionOutLocation.mkdirs()
        }
        logger.info("scope status = $scope.")
    }

    suspend fun startConversion(
            file: VideoFile,
            convSpecs: List<Pair<MediaTrack, Conversion>>
    ): Boolean {
        val newFile = File(ConversionOutLocation, file.fileName)
        // check if there is a current job running for a file
        currentJobs[file.path]?.let { job ->
            // if yes, stop it. Clean up the progress files
            job.cancelAndJoin()
            if (newFile.exists()) {
                newFile.delete()
            }
        }

        // start the new job & add it to the current jobs
        val updates = MutableStateFlow(0)
        currentProgress[file.path] = updates
        currentJobs[file.path] = scope.launch {
            startJob(file, convSpecs, newFile, updates)
        }
        logger.info("convert file ${file.fileName}")

        return true
    }

    private suspend fun startJob(
            file: VideoFile,
            convSpecs: List<Pair<MediaTrack, Conversion>>,
            outFile: File,
            updates: MutableStateFlow<Int>
    ) = coroutineScope {
        val builder = ProcessBuilder(*buildCommand(file, convSpecs, outFile).toTypedArray()).redirectErrorStream(true)
        logger.debug("Starting process: {}", builder.command().joinToString(separator = " "))
        val process = builder.start()
        // Read output and update stateflow
        launch {
            parseProcessOut(process.inputStream, updates)
        }

        while (isActive && process.isAlive) {
            // Exits out when conversion is complete or on cancellation, kills the process and exit
            try {
                delay(500.milliseconds)
            } catch (e: CancellationException) {
                logger.info("Killing process id: ${process.pid()} Command: ${process.info().commandLine()}")
                process.destroyForcibly()
            }
        }
    }

    private fun parseProcessOut(iStream: InputStream, updates: MutableStateFlow<Int>) {
        var duration: Duration? = null
        val durationRegex = Regex("^\\s*Duration: (\\d+):(\\d+):([\\d.]+).*")
        val frameRegex = Regex("frame=.*time=(\\d+):(\\d+):([\\d.]+).*")
        try {
            iStream.bufferedReader().use { s ->
                s.lines().forEach { line ->
                    if (durationRegex.matches(line)) {
                        val dur = durationRegex.replace(line, "PT$1H$2M$3S")
                        duration = Duration.parseIsoStringOrNull(dur)
                    } else if (frameRegex.matches(line)) {
                        val dur = frameRegex.replace(line, "PT$1H$2M$3S")
                        val currentDur = Duration.parseIsoStringOrNull(dur)
                        currentDur?.let { c ->
                            duration?.let { d ->
                                val percent = c.inWholeSeconds * 100 / d.inWholeSeconds
//                                logger.info("Dur= $d; Cur=$c; Completed=${percent}%")
                                updates.value = percent.toInt()
                            }
                        }
                    }
                }
            }
        } catch (_: UncheckedIOException) {
        }
    }

    internal fun buildCommand(
            file: VideoFile,
            convSpecs: List<Pair<MediaTrack, Conversion>>,
            outFile: File
    ): List<String> = listOf(
            "ffmpeg",
            "-hide_banner",
            "-i",
            file.path,
            *conversionParams(convSpecs).toTypedArray(),
            outFile.absolutePath
    )

    internal fun conversionParams(convSpecs: List<Pair<MediaTrack, Conversion>>): List<String> {
        val result = mutableListOf<String>()
        convSpecs.forEachIndexed { i, (track, conv) ->

            when (conv) {
                Conversion.Copy -> result += listOf(
                        "-map",
                        "0:${track.index}",
                        "-codec:$i", // i = stream number in output file
                        "copy"
                )

                Conversion.Drop -> {}

                is Conversion.Convert -> result += listOf(
                        "-map",
                        "0:${track.index}",
                        "-codec:$i",
                        conv.codec
                )
            }
        }
        return result
    }

    companion object {
        val ConversionOutLocation = File("/tmp/vid-con")
    }
}