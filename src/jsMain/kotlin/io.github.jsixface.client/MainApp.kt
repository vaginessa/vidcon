package io.github.jsixface.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.softwork.bootstrapcompose.Breakpoint
import app.softwork.bootstrapcompose.Container
import app.softwork.bootstrapcompose.Navbar
import app.softwork.bootstrapcompose.NavbarCollapseBehavior
import app.softwork.bootstrapcompose.NavbarLink
import app.softwork.bootstrapcompose.NavbarNav
import app.softwork.bootstrapcompose.NavbarPlacement
import app.softwork.bootstrapcompose.Row
import app.softwork.routingcompose.HashRouter
import app.softwork.routingcompose.Router
import io.github.jsixface.common.Api
import io.github.jsixface.viewmodel.SettingsViewModel
import io.github.jsixface.viewmodel.VideosViewModel
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text

@Composable
fun MainApp(app: AppContainer) {
    val api by app.api.collectAsState()
    Header(api)
    HashRouter(initPath = "videos") {
        val videosViewModel = VideosViewModel(app.client)
        route("settings") {
            ShowSettings(SettingsViewModel(app.client))
            app.api.value = Api.Settings
        }
        route("videos") {
            VideosPage(videosViewModel)
            app.api.value = Api.Videos
        }
        route("video") {
            ShowVideo(videosViewModel)
        }
        noMatch {
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
        }
    }
}