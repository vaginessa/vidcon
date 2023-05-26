package io.github.jsixface.route

import io.github.jsixface.api.VideoApi
import io.github.jsixface.common.Api
import io.github.jsixface.common.Conversion
import io.github.jsixface.common.MediaTrack
import io.github.jsixface.logger
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.post
import io.ktor.server.response.respond
import io.ktor.server.response.respondNullable
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.Routing


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

    post<Api.Videos.Video> { video ->
        video.path?.let {
            logger.info("Converting the video: ${video.path}")
            val data = call.receive<List<Pair<MediaTrack, Conversion>>>()
            logger.info("Got the data: $data")
            call.respond("OK")
        }
    }
}