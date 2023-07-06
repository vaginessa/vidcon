package io.github.jsixface.api

import io.github.jsixface.common.Conversion
import io.github.jsixface.common.MediaTrack
import io.github.jsixface.common.VideoFile
import io.github.jsixface.logger
import io.ktor.utils.io.CancellationException
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import java.io.File
import java.io.InputStream
import java.io.UncheckedIOException
import java.nio.file.Files
import java.util.UUID
import kotlin.io.path.Path
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class ConversionApi(settingsApi: SettingsApi) {
    private val logger = logger()
    private val scope = CoroutineScope(Dispatchers.IO)
    val jobs = mutableListOf<ConvertingJob>()
    private val workspace = File(settingsApi.getSettings().workspaceLocation)

    init {
        if (workspace.isDirectory.not()) {
            workspace.mkdirs()
        }
    }

    suspend fun startConversion(
            file: VideoFile,
            convSpecs: List<Pair<MediaTrack, Conversion>>
    ): Boolean {
        val jobId = UUID.randomUUID().toString()
        val newDir = File(workspace, jobId).apply { mkdirs() }
        val newFile = File(newDir, file.fileName)

        // start the new job & add it to the current jobs
        val updates = MutableStateFlow(0)
        val job = scope.launch {
            startJob(file, convSpecs, newFile, updates)
        }
        val convJob = ConvertingJob(
                videoFile = file,
                convSpecs = convSpecs,
                outFile = newFile,
                job = job,
                progress = updates,
                jobId = jobId
        )
        jobs.add(convJob)
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
        if (process.isAlive) process.destroyForcibly()
        if (process.waitFor() == 0) moveFiles(file, outFile)
    }

    private fun moveFiles(file: VideoFile, outFile: File) {
        logger.info("Move file to location")
        try {
            val bkpFile = Path("${file.path}.bkp")
            logger.info("   Backing up ${file.path}")
            Files.move(Path(file.path), bkpFile)
            logger.info("   Move ${outFile.path} to ${file.path}")
            Files.move(outFile.toPath(), Path(file.path))
            logger.info("   Delete workspace dir. ${outFile.parent}")
            Files.deleteIfExists(outFile.parentFile.toPath())
        } catch (e: Exception) {
            logger.error("Cannot move file", e)
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
        updates.value = 100
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
}

data class ConvertingJob(
        val videoFile: VideoFile,
        val convSpecs: List<Pair<MediaTrack, Conversion>>,
        val outFile: File,
        val job: Job,
        val progress: MutableStateFlow<Int> = MutableStateFlow(0),
        val jobId: String,
        val startedAt: LocalDateTime = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
)