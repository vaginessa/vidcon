package io.github.jsixface.common

import io.ktor.resources.*

sealed interface Api {

    @Resource("/videos")
    object Videos : Api {
        @Resource("{path}")
        class Video(val parent: Videos = Videos, val path: String) : Api
    }

    @Resource("/locations")
    object Locations : Api
}