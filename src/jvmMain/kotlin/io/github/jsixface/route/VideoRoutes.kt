package io.github.jsixface.route

import io.github.jsixface.api.VideoApi
import io.github.jsixface.common.Api
import io.github.jsixface.logger
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.resources.get
import io.ktor.server.response.*
import io.ktor.server.routing.*


fun Routing.video(videoApi: VideoApi) {
    val logger = logger()
    get<Api.Videos> {
        call.respond(videoApi.getVideos().values.toList())
    }
    get<Api.Videos.Video> { video ->
        logger.info("Getting video ${video.path}")
        video.path?.let {
            val find = videoApi.getVideos().values.find { v -> v.fileName == it }
            logger.info("found = $find")
            call.respondNullable(find)
        } ?: run {
            call.respondRedirect("/videos")
        }
    }
    post("convert") {
        call.receiveParameters()
    }
}