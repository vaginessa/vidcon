package io.github.jsixface

import io.github.jsixface.plugins.configureHTTP
import io.github.jsixface.plugins.configureKoin
import io.github.jsixface.plugins.configureRouting
import io.ktor.server.application.Application
import org.slf4j.Logger
import org.slf4j.LoggerFactory

fun main(args: Array<String>): Unit =
        io.ktor.server.cio.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function.
// This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    configureKoin()
    configureHTTP()
    configureRouting()
}


inline fun <reified T> T.logger(): Logger {
    if (T::class.isCompanion) {
        return LoggerFactory.getLogger(T::class.java.enclosingClass)
    }
    return LoggerFactory.getLogger(T::class.java)
}
