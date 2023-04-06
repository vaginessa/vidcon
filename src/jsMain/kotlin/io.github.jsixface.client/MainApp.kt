package io.github.jsixface.client

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.softwork.bootstrapcompose.*
import app.softwork.routingcompose.HashRouter
import app.softwork.routingcompose.Router
import app.softwork.routingcompose.Routing
import io.github.jsixface.common.Api
import org.jetbrains.compose.web.dom.H1
import org.jetbrains.compose.web.dom.Li
import org.jetbrains.compose.web.dom.Text

@Composable
fun MainApp(app: AppContainer) {
    val api by app.api.collectAsState()
    Header(api)

    HashRouter(initPath = "locations") {
        route("locations" ){
            Text("Current route = ${Router.current.currentPath}")
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
                    active = api == Api.Locations,
                    link = "#/locations"
                ) {  Text("Locations") }
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