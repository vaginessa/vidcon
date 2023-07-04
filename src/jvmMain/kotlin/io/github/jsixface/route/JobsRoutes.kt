package io.github.jsixface.route

import io.github.jsixface.api.JobsApi
import io.github.jsixface.common.Api
import io.github.jsixface.common.Settings
import io.github.jsixface.logger
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.resources.get
import io.ktor.server.resources.delete
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import org.koin.ktor.ext.inject

fun Routing.jobRoutes() {
    val jobsApi by inject<JobsApi>()
    val logger = logger()

    get<Api.Jobs> {
        val jobs = jobsApi.getJobs()
        call.respond(jobs)
    }

    delete<Api.Jobs.Job> { job ->
        logger.info("Going to delete $job")
        jobsApi.stopJob(job.id)
        call.respond(job)
    }
}