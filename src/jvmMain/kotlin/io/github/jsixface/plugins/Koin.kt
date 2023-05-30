package io.github.jsixface.plugins

import io.github.jsixface.api.ConversionApi
import io.github.jsixface.api.LocationApi
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
    single { ConversionApi() }
    single { LocationApi() }
    single { VideoApi() }
}
