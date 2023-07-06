package io.github.jsixface.client

import io.github.jsixface.common.Api
import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.resources.Resources
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.serialization.json.Json

class AppContainer {

    val client = HttpClient(Js) {

        install(Resources)

        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
            })
        }
    }


    val api: MutableStateFlow<Api> = MutableStateFlow(Api.Videos)
}