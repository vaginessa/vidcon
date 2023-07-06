package io.github.jsixface.utils

import io.github.jsixface.common.MediaProbeInfo
import io.github.jsixface.common.MediaTrack
import io.github.jsixface.common.TrackType
import io.github.jsixface.logger
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

object CodecUtils {
    private val logger = logger()
    private val json = Json {
        ignoreUnknownKeys = true
    }

    fun parseMediaInfo(path: String): List<MediaTrack> {
        logger.info("Parsing file $path")
        val builder =
                ProcessBuilder("ffprobe", "-v", "error", "-show_entries", "stream", "-pretty", "-of", "json", path)
        val process = builder.start()
        val output = process.inputStream.use { it.bufferedReader().readText() }
        process.waitFor()
        val probeInfo: MediaProbeInfo = json.decodeFromString(output)
        return probeInfo.streams.mapNotNull { s ->
            val trackType = when (s.codecType) {
                "audio" -> TrackType.Audio
                "video" -> TrackType.Video
                "subtitle" -> TrackType.Subtitle
                else -> null
            }
            trackType?.let { MediaTrack(trackType, s.index, s.codecName) }
        }
    }
}
