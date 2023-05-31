package io.github.jsixface.plugins

import io.github.jsixface.api.ConversionApi
import io.github.jsixface.api.SettingsApi
import io.github.jsixface.api.VideoApi
import io.ktor.server.application.Application
import org.koin.dsl.module
import org.koin.ktor.plugin.koin

fun Application.configureKoin() {
    koin {
        modules(koinModule)
    }
}

private val koinModule = module {
    single { SettingsApi() }
    single { VideoApi() }
    single { ConversionApi(settingsApi = get()) }
}
