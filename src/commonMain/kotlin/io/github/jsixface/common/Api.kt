package io.github.jsixface.common

import io.ktor.resources.*

sealed interface Api {

    @Resource("/videos")
    object Videos : Api {
        @Resource("video")
        data class Video(val parent: Videos = Videos, val path: String?) : Api
    }

    @Resource("/settings")
    object Settings : Api

    @Resource("/jobs")
    object Jobs : Api {
        @Resource("{id}")
        data class Job(val parent: Jobs = Jobs, val id: String): Api
    }
}