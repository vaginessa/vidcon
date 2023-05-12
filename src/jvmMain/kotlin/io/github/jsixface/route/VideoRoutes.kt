package io.github.jsixface.route

import io.github.jsixface.api.VideoApi
import io.github.jsixface.common.Api
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Routing.video(videoApi: VideoApi) {

        get<Api.Videos> {
            call.respond(videoApi.getVideos().values.toList())
        }
        get<Api.Videos.Video> { video ->
            video.path?.let { videoApi.getVideos()[it] } ?: run {
                call.respondRedirect("/videos")
            }
        }
        post("convert") {
            call.receiveParameters()
        }
}