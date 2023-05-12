package io.github.jsixface.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.softwork.bootstrapcompose.Container
import app.softwork.routingcompose.Router
import app.softwork.routingcompose.navigate
import io.github.jsixface.viewmodel.VideoViewModel
import io.github.jsixface.viewmodel.ViewModels
import kotlinx.coroutines.Dispatchers
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Table
import org.jetbrains.compose.web.dom.Tbody
import org.jetbrains.compose.web.dom.Td
import org.jetbrains.compose.web.dom.Text
import org.jetbrains.compose.web.dom.Th
import org.jetbrains.compose.web.dom.Thead
import org.jetbrains.compose.web.dom.Tr


@Composable
fun VideosPage(viewModel: ViewModels) {
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
                            Button(attrs = {
                                onClick {
                                    router.navigate(
                                        "/video",
                                        mapOf("path" to v.fileName)
                                    )
                                }
                            }) { Text("Convert") }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShowVideo(viewModel: VideoViewModel) {
    val router = Router.current
    val videoFile by viewModel.vidFile.collectAsState()
    router.currentPath.parameters?.map?.get("path")?.firstOrNull()?.let { file ->
        viewModel.loadFile(file)
    }

    Form {
        Div {
            H2 { Text() }
        }
    }
}