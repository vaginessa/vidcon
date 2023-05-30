package io.github.jsixface.api

import io.github.jsixface.common.Conversion
import io.github.jsixface.common.MediaTrack
import io.github.jsixface.common.TrackType
import io.github.jsixface.common.VideoFile
import org.junit.Assert.*
import org.junit.Test
import java.io.File

class ConversionApiTest {

    private val conversionApi = ConversionApi()

    @Test
    fun testConvCommand() {

        val audioTrack = MediaTrack(TrackType.Audio, 1, "mp3")
        var conv: List<Pair<MediaTrack, Conversion>> = listOf(audioTrack to Conversion.Copy)
        assertEquals(
            listOf("-map", "0:a:1", "-codec:a:1", "copy"),
            conversionApi.conversionParams(conv)
        )

        conv = listOf(audioTrack to Conversion.Convert(codec = "aac"))

        assertEquals(
            listOf("-map", "0:a:1", "-codec:a:1", "aac"),
            conversionApi.conversionParams(conv)
        )
    }

    @Test
    fun testFullCommand() {
        val file = VideoFile(
            path = "/tmp/abc",
            fileName = "filename",
            modifiedTime = 2
        )
        val audioTrack = MediaTrack(TrackType.Audio, 1, "mp3")
        var conv: List<Pair<MediaTrack, Conversion>> = listOf(audioTrack to Conversion.Copy)
        assertEquals(
            listOf(
                "ffmpeg",
                "-hide_banner",
                "-i",
                "/tmp/abc",
                "-map",
                "0:a:1",
                "-codec:a:1",
                "copy",
                "/tmp/outfile"
            ),
            conversionApi.buildCommand(file, conv, File("/tmp", "outfile"))
        )


    }
}