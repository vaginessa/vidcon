package io.github.jsixface.viewmodel

import io.github.jsixface.common.Api
import io.github.jsixface.common.Settings
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.resources.get
import io.ktor.client.plugins.resources.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import scope

class SettingsViewModel(private val client: HttpClient) {

    private val _settings = MutableStateFlow<Settings?>(null)
    val settings: StateFlow<Settings?> = _settings

    init {
        loadSettings()
    }

    private fun loadSettings() {
        scope.launch {
            _settings.value = client.get(Api.Settings).body()
        }
    }

    fun save(locations: List<String>, extension: List<String>, workLocation: String) {
        scope.launch {
            val settings = Settings(libraryLocations = locations, workspaceLocation = workLocation, videoExtensions = extension)
            console.log("New settings: $settings")
            // Key cannot be converted to json
            val saved: Settings = client.post(Api.Settings) {
                contentType(ContentType.Application.Json)
                setBody(settings)
            }.body()
            _settings.value = saved
        }
    }
}
