package io.github.jsixface.client.viewModels

import io.github.jsixface.common.Api
import io.github.jsixface.common.ConversionJob
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.delete
import io.ktor.client.plugins.resources.get
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.seconds

class JobsViewModel(private val client: HttpClient) : ViewModel() {

    private val _jobLists = MutableStateFlow(emptyList<ConversionJob>())
    val jobList: StateFlow<List<ConversionJob>> = _jobLists

    companion object {
        private var refreshJob: Job? = null
    }

    init {
        refreshJob?.cancel()
        refreshJob = scope.launch {
            while (isActive) {
                _jobLists.value = client.get(Api.Jobs).body()
                delay(2.seconds)
            }
        }
    }

    fun clearJobs() {
        scope.launch {
            console.log("Clearing finished jobs")
            client.delete(Api.Jobs)
        }
    }

    fun cancelJob(id: String) {
        scope.launch {
            console.log("Cancelling job $id")
            client.delete(Api.Jobs.Job(id = id)) {
                contentType(ContentType.Application.Json)
            }
        }
    }
}