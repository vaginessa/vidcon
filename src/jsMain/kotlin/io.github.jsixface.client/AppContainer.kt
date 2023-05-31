package io.github.jsixface.client

import io.github.jsixface.common.Api
import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.resources.*
import io.ktor.serialization.kotlinx.json.*
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