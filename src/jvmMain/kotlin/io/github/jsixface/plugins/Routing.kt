package io.github.jsixface.plugins

import io.github.jsixface.api.LocationApi
import io.github.jsixface.api.VideoApi
import io.github.jsixface.route.location
import io.github.jsixface.route.video
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {

    val videoApi = VideoApi()
    val locationApi = LocationApi()

    routing {
        static("/static") {
            resources("files")
        }

        get("/") {
            call.respondText(
                this::class.java.classLoader.getResource("index.html")!!.readText(),
                ContentType.Text.Html
            )
        }
        static("/") {
            resources("")
        }

        video(videoApi)
        location(locationApi)

    }
}