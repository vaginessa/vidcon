package io.github.jsixface.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import app.softwork.bootstrapcompose.Button
import app.softwork.bootstrapcompose.Column
import app.softwork.bootstrapcompose.Container
import app.softwork.bootstrapcompose.FormLabel
import app.softwork.bootstrapcompose.Row
import app.softwork.bootstrapcompose.Select
import app.softwork.bootstrapcompose.SelectContext
import app.softwork.bootstrapcompose.SelectSize
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
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.H4
import org.jetbrains.compose.web.dom.Hr
import org.jetbrains.compose.web.dom.Table
import org.jetbrains.compose.web.dom.Tbody
import org.jetbrains.compose.web.dom.Td
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Th
import org.jetbrains.compose.web.dom.Thead
import org.jetbrains.compose.web.dom.Tr


@Composable
fun VideosPage(viewModel: VideosViewModel) {
    val videos by viewModel.videos.collectAsState(listOf(), Dispatchers.Default)
    val router = Router.current

    Container {
        Table(attrs = { classes("table") }) {
            Thead {
                Tr {
                    Th { Text("Filename") }
                    Th { Text("Audio Codecs") }
                    Th { Text("Video Codecs") }
                    Th { Text("Modified time") }
                    Th { Text("") }
                }
            }
            Tbody {
                videos.forEach { v ->
                    Tr {
                        Td { Text(v.fileName) }
                        Td { Text(v.audioInfo) }
                        Td { Text(v.videoInfo) }
                        Td { Text(v.modified) }
                        Td {
                            Button(
                                title = "Convert",
                                attrs = { classes("btn-primary") }) {
                                router.navigate(
                                    "/video",
                                    mapOf("path" to v.fileName)
                                )
                            }
                        }
                    }
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
//                Div(attrs = { classes("col-md-8", "offset-md-2", "mt-5") }) {
                    H2(attrs = { classes("text-center", "my-3") }) { Text(v.fileName) }
                    Form(attrs = {onSubmit { it.preventDefault() }}) {
                        Row {
                            Column(size = 6) {
                                H4(attrs = { classes("text-center", "mb-3") }) { Text("Audio") }
                                v.audios.forEach { a -> StreamView(a) { conversion[a] = it } }
                                Hr { }
                            }
                            Column(size = 6) {
                                H4(attrs = { classes("text-center", "mb-3") }) { Text("Video") }
                                StreamView(v.videos[0]) { conversion[v.videos[0]] = it }
                                Hr { }
                            }
                        }
                        Row {
                            Button(
                                title = "Convert",
                                attrs = { classes("btn-success", "btn-lg") }) {
                                console.log("Conversion list: $conversion")
                            }
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
            Column(size = 6) { FormLabel { Text("Codec: ${track.codec}") } }
            Column(size = 6) { FormLabel { Text("Index: ${track.number}") } }
        }
        Select(size = SelectSize.Large, multiple = false, onChange = {
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