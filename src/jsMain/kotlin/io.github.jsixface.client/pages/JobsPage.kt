package io.github.jsixface.client.pages

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.softwork.bootstrapcompose.Column
import app.softwork.bootstrapcompose.Row
import io.github.jsixface.client.viewModels.JobsViewModel
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Text

@Composable
fun ShowJobList(viewModel: JobsViewModel) {
    val jobList by viewModel.jobList.collectAsState(emptyList())

    H1 {
        Text("Job List")
    }
    jobList.forEach {
        Row {
            Column { Text(it.jobId) }
            Column { Text(it.progress.toString()) }
            Column { Text(it.status.name) }
            Column { Text(it.file.fileName) }
        }
    }
}