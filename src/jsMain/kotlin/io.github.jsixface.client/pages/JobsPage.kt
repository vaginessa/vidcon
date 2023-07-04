package io.github.jsixface.client.pages

import androidx.compose.runtime.*
import app.softwork.bootstrapcompose.*
import io.github.jsixface.client.viewModels.JobsViewModel
import io.github.jsixface.common.JobStatus
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text

@Composable
fun ShowJobList(viewModel: JobsViewModel) {
    val jobList by viewModel.jobList.collectAsState(emptyList())
    val rowsPerPage = remember { mutableStateOf(20) }

    if (jobList.isEmpty()) return

    Container {
        H1 {
            Text("Job List")
        }
        Table(
                pagination = Table.OffsetPagination(
                        data = jobList,
                        entriesPerPageLimit = rowsPerPage
                ),
                stripedRows = true,
                fixedHeader = Table.FixedHeaderProperty(
                        topSize = 50.px,
                        zIndex = ZIndex(1000)
                )
        ) { _, job ->
            column("Job ID") { Text(job.jobId) }
            column("Progress") { Text(job.progress.toString()) }
            column("Status") { Text(job.status.name) }
            column("Started At") { Text(job.startedAt) }
            column("File") { Text(job.file.fileName) }
            column("") {
                if (job.status == JobStatus.InProgress) {
                    Button(title = "Cancel", color = Color.Success) {
                        viewModel.cancelJob(job.jobId)
                    }
                } else {
                    Text("No Action")
                }
            }
        }
    }
}