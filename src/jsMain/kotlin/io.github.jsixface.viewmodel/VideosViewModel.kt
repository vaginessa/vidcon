package io.github.jsixface.viewmodel

import io.github.jsixface.common.Api
import io.github.jsixface.common.VideoFile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import scope

class VideosViewModel(private val client: HttpClient) {

    private val _videos = MutableStateFlow(listOf<VideoFile>())
    val videos: Flow<List<VideoFile>> = _videos
    private var videoList = listOf<VideoFile>()

    init {
        loadVideos()
    }

    private fun loadVideos() {
        scope.launch {
            _videos.value = client.get(Api.Videos).body()
            videoList = _videos.value
        }
    }

    fun loadFile(file: String): Flow<VideoFile?> = flow {
        emit(client.get(Api.Videos.Video(path = file)).body())
    }
}
