package io.github.jsixface.client.viewModels

import io.github.jsixface.common.Api
import io.github.jsixface.common.Conversion
import io.github.jsixface.common.MediaTrack
import io.github.jsixface.common.VideoFile
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class VideosViewModel(private val client: HttpClient) : ViewModel() {

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

    fun convert(file: String, conversionList: Map<MediaTrack, Conversion>, onComplete: () -> Unit) {
        scope.launch {
            val toConvert = conversionList.entries.map { it.toPair() }
            console.log("Convert List: $toConvert")
            // Key cannot be converted to json
            client.post(Api.Videos.Video(path = file)) {
                contentType(ContentType.Application.Json)
                setBody(toConvert)
            }
            onComplete()
        }
    }
}
