package io.github.jsixface.client.pages

import androidx.compose.runtime.*
import app.softwork.bootstrapcompose.*
import app.softwork.bootstrapcompose.Table.FixedHeaderProperty
import app.softwork.routingcompose.Router
import app.softwork.routingcompose.navigate
import io.github.jsixface.client.viewModels.VideosViewModel
import io.github.jsixface.common.*
import io.github.jsixface.common.TrackType.*
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.*


@Composable
fun VideosPage(viewModel: VideosViewModel) {
    val videos by viewModel.videos.collectAsState(listOf(), Dispatchers.Default)
    val router = Router.current
    val rowsPerPage = remember { mutableStateOf(20) }
    var filterName by remember { mutableStateOf("") }
    var filterAudio by remember { mutableStateOf("") }
    var filterVideo by remember { mutableStateOf("") }

    val filteredVideos = videos.filter {
        if (filterName.length >= 3) it.fileName.lowercase().contains(filterName.lowercase()) else true
    }.filter { v ->
        if (filterAudio.isNotEmpty()) v.audios.any { c -> c.codec == filterAudio } else true
    }.filter { v ->
        if (filterVideo.isNotEmpty()) v.videos.any { c -> c.codec == filterVideo } else true
    }.sortedBy {
        it.fileName
    }
    val aCodecs = videos.flatMap { it.audios }.map { it.codec }.toSet()
    val vCodecs = videos.flatMap { it.videos }.map { it.codec }.toSet()

    Container(attrs = { classes("my-3") }) {
        Row {
            Column(size = 10, attrs = { classes("offset-md-1") }) {
                Row {
                    Column(size = 2) { FormLabel { Text("File Name") } }
                    Column { InputGroup { TextInput(value = filterName) { filterName = it.value } } }
                }
                Row {
                    Column(size = 2) { FormLabel { Text("Audio codec") } }
                    Column {
                        Select(size = SelectSize.Default, multiple = false, onChange = { filterAudio = it.first() }) {
                            Option("") { Text("") }
                            aCodecs.forEach { Option(it) { Text(it) } }
                        }
                    }
                }
                Row {
                    Column(size = 2) { FormLabel { Text("Video codec") } }
                    Column {
                        Select(size = SelectSize.Default, multiple = false, onChange = { filterVideo = it.first() }) {
                            Option("") { Text("") }
                            vCodecs.forEach { Option(it) { Text(it) } }
                        }
                    }
                }
                Row(attrs = { classes("mt-3") }) {
                    Column(size = 2, attrs = { classes("offset-md-4") }) {
                        Button(title = "Refresh", color = Color.Info) { viewModel.refresh() }
                    }
                }
            }
        }
    }
    if (filteredVideos.isNotEmpty()) {
        Container {
            Table(
                    pagination = Table.OffsetPagination(
                            data = filteredVideos,
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
                        Row(attrs = { classes("mb-3") }) {
                            H4(attrs = { classes("text-center") }) { Text("Audio") }
                            v.audios.forEach { a -> StreamView(a) { conversion[a] = it } }
                        }
                        if (v.subtitles.isNotEmpty()) {
                            Row(attrs = { classes("mb-3") }) {
                                H4(attrs = { classes("text-center") }) { Text("Subtitle") }
                                v.subtitles.forEach { a -> StreamView(a) { conversion[a] = it } }
                            }
                        }
                        Row(attrs = { classes("mb-3") }) {
                            H4(attrs = { classes("text-center") }) { Text("Video") }
                            StreamView(v.videos[0]) { conversion[v.videos[0]] = it }
                        }
                        Row(attrs = { classes("mb-3") }) {
                            Column(size = 4) {}
                            Column(size = 4) {
                                Button(
                                        title = "Convert",
                                        attrs = { classes("btn-success", "btn-lg") }) {
                                    console.log("Conversion list: $conversion")
                                    (v.videos + v.audios + v.subtitles).forEach {
                                        conversion.getOrPut(it) { Conversion.Copy }
                                    }
                                    viewModel.convert(v.fileName, conversion) {
                                        router.navigate("/jobs")
                                    }
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