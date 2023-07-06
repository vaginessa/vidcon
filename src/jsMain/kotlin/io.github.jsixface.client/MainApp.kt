package io.github.jsixface.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.softwork.bootstrapcompose.*
import app.softwork.routingcompose.HashRouter
import app.softwork.routingcompose.Router
import io.github.jsixface.client.pages.ShowJobList
import io.github.jsixface.client.pages.ShowSettings
import io.github.jsixface.client.pages.ShowVideo
import io.github.jsixface.client.pages.VideosPage
import io.github.jsixface.client.viewModels.JobsViewModel
import io.github.jsixface.client.viewModels.SettingsViewModel
import io.github.jsixface.client.viewModels.VideosViewModel
import io.github.jsixface.client.viewModels.ViewModel
import io.github.jsixface.common.Api
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text

@Composable
fun MainApp(app: AppContainer) {
    val api by app.api.collectAsState()
    var vm: ViewModel? = null

    Header(api)
    HashRouter(initPath = "videos") {
        route("settings") {
            vm?.destroy()
            val settingsViewModel = SettingsViewModel(app.client)
            ShowSettings(settingsViewModel)
            vm = settingsViewModel
            app.api.value = Api.Settings
        }
        route("videos") {
            vm?.destroy()
            val videosViewModel = VideosViewModel(app.client)
            vm = videosViewModel
            VideosPage(videosViewModel)
            app.api.value = Api.Videos
        }
        route("video") {
            vm?.destroy()
            val videosViewModel = VideosViewModel(app.client)
            vm = videosViewModel
            ShowVideo(videosViewModel)
        }
        route("jobs") {
            vm?.destroy()
            val jobsViewModel = JobsViewModel(app.client)
            vm = jobsViewModel
            ShowJobList(jobsViewModel)
            app.api.value = Api.Jobs
        }
        noMatch {
            vm?.destroy()
            Text("Current route = ${Router.current.currentPath}")
        }
    }
}

@Composable
fun Header(api: Api) {
    Container {
        Row {
            H1 { Text("Video Convertor") }
        }
        Row {
            TopNav(api)
        }
    }
}

@Composable
fun TopNav(api: Api) {
    Navbar(
            placement = NavbarPlacement.StickyTop,
            collapseBehavior = NavbarCollapseBehavior.AtBreakpoint(Breakpoint.Large),
    ) {
        NavbarNav {
            Li(attrs = { classes("nav-item") }) {
                NavbarLink(
                        active = api == Api.Settings,
                        link = "#/settings"
                ) { Text("Settings") }
            }
            Li(attrs = { classes("nav-item") }) {
                NavbarLink(
                        active = api == Api.Videos,
                        link = "#/videos"
                ) { Text("Videos") }
            }
            Li(attrs = { classes("nav-item") }) {
                NavbarLink(
                        active = api == Api.Jobs,
                        link = "#/jobs"
                ) { Text("Jobs") }
            }
        }
    }
}