package io.github.jsixface.common

import io.ktor.resources.*

sealed interface Api {

    @Resource("/videos")
    object Videos : Api {
        @Resource("video")
        class Video(val parent: Videos = Videos, val path: String?) : Api
    }

    @Resource("/settings")
    object Settings : Api

    @Resource("/jobs")
    object Jobs : Api {
        @Resource("{id}")
        class Job(val parent: Jobs = Jobs, val id: String): Api
    }
}