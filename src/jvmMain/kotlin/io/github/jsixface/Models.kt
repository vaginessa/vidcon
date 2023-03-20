package io.github.jsixface

import kotlinx.serialization.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

enum class TrackType {
    Video, Audio, Subtitle
}

@Serializable
data class MediaTrack(
    val type: TrackType,
    val number: Int,
    val codec: String
)

@Serializable
data class VideoFile(
    val path: String,
    val fileName: String,
    val modifiedTime: Long,
    val audios: List<MediaTrack> = listOf(),
    val videos: List<MediaTrack> = listOf(),
    val subtitles: List<MediaTrack> = listOf()
) {
    @Transient
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    val videoInfo: String
        get() = videos.joinToString { it.codec }

    val audioInfo: String
        get() = audios.joinToString { it.codec }

    val subtitleInfo: String
        get() = subtitles.joinToString { it.codec }

    val modified: String
        get() = LocalDateTime.ofInstant(Instant.ofEpochMilli(modifiedTime), ZoneId.systemDefault())
            .format(formatter)
}

@Serializable
data class MediaStream(
    val index: Int,
    @SerialName("codec_name")
    val codecName: String,
    @SerialName("codec_type")
    val codecType: String,
    val channels: Int = 1
)

@Serializable
data class MediaProbeInfo(
    val streams: List<MediaStream>
)

class Context(val content: MutableMap<String, Any> = mutableMapOf()) : MutableMap<String, Any> by content {
    fun error(e: Exception) {
        content[e.javaClass.simpleName] = e.localizedMessage
    }
}