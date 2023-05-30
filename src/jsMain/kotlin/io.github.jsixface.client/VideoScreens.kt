package io.github.jsixface.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.softwork.bootstrapcompose.Button
import app.softwork.bootstrapcompose.Color
import app.softwork.bootstrapcompose.Column
import app.softwork.bootstrapcompose.Container
import app.softwork.bootstrapcompose.FormLabel
import app.softwork.bootstrapcompose.Row
import app.softwork.bootstrapcompose.Select
import app.softwork.bootstrapcompose.SelectContext
import app.softwork.bootstrapcompose.SelectSize
import app.softwork.bootstrapcompose.Table
import app.softwork.bootstrapcompose.Table.FixedHeaderProperty
import app.softwork.bootstrapcompose.ZIndex
import app.softwork.routingcompose.Router
import app.softwork.routingcompose.navigate
import io.github.jsixface.common.AudioCodecs
import io.github.jsixface.common.Conversion
import io.github.jsixface.common.MediaTrack
import io.github.jsixface.common.SubtitleCodecs
import io.github.jsixface.common.TrackType
import io.github.jsixface.common.TrackType.Audio
import io.github.jsixface.common.TrackType.Subtitle
import io.github.jsixface.common.TrackType.Video
import io.github.jsixface.common.VideoCodecs
import io.github.jsixface.viewmodel.VideosViewModel
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.Text


@Composable
fun VideosPage(viewModel: VideosViewModel) {
    val videos by viewModel.videos.collectAsState(listOf(), Dispatchers.Default)
    val router = Router.current
    val rowsPerPage = remember { mutableStateOf(20) }
    if (videos.isEmpty()) return

    Container {
        Table(
            pagination = Table.OffsetPagination(
                data = videos,
                entriesPerPageLimit = rowsPerPage
            ),
            stripedRows = true,
            fixedHeader = FixedHeaderProperty(
                topSize = 50.px,
                zIndex = ZIndex(1000)
            )
        ) { _, file ->
            column("Filename") { Text(file.fileName) }
            column("Audio Codecs") { Text(file.audioInfo) }
            column("Video Codecs") { Text(file.videoInfo) }
            column("Modified") { Text(file.modified) }
            column("") {
                Button(title = "Convert", color = Color.Success) {
                    router.navigate("/video", mapOf("path" to file.fileName))
                }
            }
        }
    }
}

@Composable
fun ShowVideo(viewModel: VideosViewModel) {
    val router = Router.current
    val videoPath = router.currentPath.parameters?.map?.get("path")?.firstOrNull() ?: return
    val videoFile by viewModel.loadFile(videoPath).collectAsState(null)
    console.log("path  = $videoPath; Video file = $videoFile")
    val conversion by remember { mutableStateOf<MutableMap<MediaTrack, Conversion>>(mutableMapOf()) }

    videoFile?.let { v ->
        Container {
            Row {
                Column(size = 8, attrs = { classes("offset-md-2") }) {
                    H2(attrs = { classes("text-center", "my-3") }) { Text(v.fileName) }
                    Form(attrs = { onSubmit { it.preventDefault() } }) {
                        Row {
                            H4(attrs = { classes("text-center", "mb-3") }) { Text("Audio") }
                            v.audios.forEach { a -> StreamView(a) { conversion[a] = it } }
                        }
                        if (v.subtitles.isNotEmpty()) {
                            Row {
                                H4(attrs = { classes("text-center", "mb-3") }) { Text("Subtitle") }
                                v.subtitles.forEach { a -> StreamView(a) { conversion[a] = it } }
                            }
                        }
                        Row {
                            H4(attrs = { classes("text-center", "mb-3") }) { Text("Video") }
                            StreamView(v.videos[0]) { conversion[v.videos[0]] = it }
                        }
                        Row {
                            Column(size = 4) {}
                            Column(size = 4) {
                                Button(
                                    title = "Convert",
                                    attrs = { classes("btn-success", "btn-lg") }) {
                                    console.log("Conversion list: $conversion")
                                    (v.videos + v.audios + v.subtitles).forEach {
                                        conversion.getOrPut(it) { Conversion.Copy }
                                    }
                                    viewModel.convert(v.fileName, conversion)
                                }
                            }
                            Column(size = 4) {}
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StreamView(track: MediaTrack, onSelect: (Conversion) -> Unit) {
    FormGroup {
        Row {
            Column(size = 3) { FormLabel { Text("Codec: ${track.codec}") } }
            Column(size = 3) { FormLabel { Text("Index: ${track.index}") } }
            Column(size = 6) {
                Select(size = SelectSize.Default, multiple = false, onChange = {
                    when (it.first()) {
                        "" -> onSelect(Conversion.Copy)
                        "drop" -> onSelect(Conversion.Drop)
                        else -> onSelect(Conversion.Convert(it.first()))
                    }
                }) {
                    selectOptions(track.type)
                }
            }
        }
    }
}

@Composable
fun SelectContext.selectOptions(type: TrackType) {
    val codecs = when (type) {
        Video -> VideoCodecs
        Audio -> AudioCodecs
        Subtitle -> SubtitleCodecs
    }

    Option("", selected = true) { Text("Copy") }
    Option("drop") { Text("Drop") }
    codecs.forEach { Option(it) { Text(it) } }
}

@Composable
fun FormGroup(content: @Composable () -> Unit) {
    Div(attrs = { classes("form-group") }) { content() }
}