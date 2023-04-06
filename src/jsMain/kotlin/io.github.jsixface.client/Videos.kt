package io.github.jsixface.client

import androidx.compose.runtime.Composable
import io.github.jsixface.common.Api
import io.ktor.client.plugins.resources.*
import io.ktor.client.request.*
import io.ktor.client.request.get
import kotlinx.coroutines.launch
import scope


@Composable
fun Videos(api: Api, app: AppContainer) {


    scope.launch {
        (api as? Api.Locations)?.let { loc ->
            app.client.get(loc)
        }
    }
}