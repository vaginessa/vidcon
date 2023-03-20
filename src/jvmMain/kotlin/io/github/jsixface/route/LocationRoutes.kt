package io.github.jsixface.route

import io.github.jsixface.api.LocationApi
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.io.File

fun Route.location(locationApi: LocationApi) {
    route("locations") {
        get {
                val a = call.request.queryParameters["a"]
                val v = call.request.queryParameters["val"]
                if (a == "del" && v?.isNotBlank() == true) locationApi.removeLocation(v)
              locationApi.getLocations()
        }

        post {
            call.receiveParameters()["newLoc"]?.let { File(it) }?.let {
                if (it.isDirectory) locationApi.addLocation(it.path)
            }
            call.respondRedirect("/locations")
        }
    }
}