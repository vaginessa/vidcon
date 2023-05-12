package io.github.jsixface.viewmodel

import androidx.compose.runtime.internal.composableLambdaInstance
import io.github.jsixface.common.Api
import io.github.jsixface.common.VideoFile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import scope

class ViewModels(client: HttpClient) {

    private val _videos = MutableStateFlow(listOf<VideoFile>())
    val videos: Flow<List<VideoFile>> = _videos

    init {
        scope.launch {
            _videos.value = client.get(Api.Videos).body()
        }
    }

}

class VideoViewModel(private val client: HttpClient) {
    val vidFile = MutableStateFlow<VideoFile?>(null)

    fun loadFile(file: String) = scope.launch {
        vidFile.value = client.get(Api.Videos.Video(path = file)).body()
    }
}