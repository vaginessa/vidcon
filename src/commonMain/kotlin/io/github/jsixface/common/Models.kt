package io.github.jsixface.common

import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


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
    val videoInfo: String
        get() = videos.joinToString { it.codec }

    val audioInfo: String
        get() = audios.joinToString { it.codec }

    val subtitleInfo: String
        get() = subtitles.joinToString { it.codec }

    val modified: String
        get()  {
            val dateTime = Instant.fromEpochMilliseconds(modifiedTime)
                .toLocalDateTime(TimeZone.currentSystemDefault())
            return "${dateTime.date} ${dateTime.time}"
        }

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
