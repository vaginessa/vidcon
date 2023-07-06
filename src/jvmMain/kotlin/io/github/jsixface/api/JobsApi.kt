package io.github.jsixface.api

import io.github.jsixface.common.ConversionJob
import io.github.jsixface.common.JobStatus
import kotlinx.coroutines.cancelAndJoin

class JobsApi(private val conversionApi: ConversionApi) {

    fun getJobs(): List<ConversionJob> = conversionApi.jobs.map {
        ConversionJob(jobId = it.jobId,
                progress = it.progress.value,
                file = it.videoFile,
                status = when (it.progress.value) {
                    0 -> JobStatus.NotStarted
                    100 -> JobStatus.Completed
                    -1 -> JobStatus.Failed
                    else -> JobStatus.InProgress
                },
                startedAt = "${it.startedAt.date} ${it.startedAt.time}"
        )
    }

    suspend fun stopJob(jobId: String) {
        conversionApi.jobs.find { it.jobId == jobId }?.let {
            it.job.cancelAndJoin()
            it.progress.value = -1
            it.outFile.deleteOnExit()
        }
    }

}