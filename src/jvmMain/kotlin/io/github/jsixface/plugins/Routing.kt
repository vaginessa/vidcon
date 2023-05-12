package io.github.jsixface.plugins

import io.github.jsixface.api.LocationApi
import io.github.jsixface.api.VideoApi
import io.github.jsixface.route.location
import io.github.jsixface.route.video
import io.ktor.http.ContentType
import io.ktor.server.application.Application
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.http.content.resources
import io.ktor.server.http.content.static
import io.ktor.server.resources.Resources
import io.ktor.server.response.respondText
import io.ktor.server.routing.get
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    val videoApi = VideoApi()
    val locationApi = LocationApi()
    install(Resources)
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