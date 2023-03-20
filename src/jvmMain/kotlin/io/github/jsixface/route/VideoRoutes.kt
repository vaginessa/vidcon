package io.github.jsixface.route

import io.github.jsixface.api.VideoApi
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Route.video(videoApi: VideoApi) {
    route("videos") {
        get {
                 videoApi.getVideos().values
        }
        get("edit") {
            call.request.queryParameters["file"]?.let { videoApi.getVideos()[it] }?.let { file ->
                    file
            } ?: run {
                call.respondRedirect("/videos")
            }
        }
        post("convert") {
            call.receiveParameters()
        }
    }
}