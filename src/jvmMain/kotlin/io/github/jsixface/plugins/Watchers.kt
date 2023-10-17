package io.github.jsixface.plugins

import io.github.jsixface.api.SavedData
import io.github.jsixface.api.VideoApi
import io.github.jsixface.logger
import io.ktor.server.application.*
import kotlinx.coroutines.*
import org.koin.ktor.ext.inject
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardWatchEventKinds.*
import kotlin.io.path.pathString


fun Application.configureWatchers() {

    val videoApi by inject<VideoApi>()
    val logger = logger()

    val savedData = SavedData.load()
    val firstDir = savedData.settings.libraryLocations.firstOrNull() ?: return
    try {
        val watchService = Paths.get(firstDir).fileSystem.newWatchService()
        environment.monitor.subscribe(ApplicationStopping) { watchService.close() }
        savedData.settings.libraryLocations.forEach {
            Paths.get(it).register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY)
        }
        CoroutineScope(Dispatchers.Default).launch {
            // Start the infinite polling loop
            while (isActive) {
                val key = withContext(Dispatchers.IO) { watchService.take() }
                val directory = (key.watchable() as? Path) ?: continue
                for (event in key.pollEvents()) {
                    val path = (event.context() as? Path) ?: continue
                    val eventFile = File(directory.toFile(), path.pathString)

                    logger.info("file: ${eventFile.absolutePath} has event ${event.kind()}")

                    videoApi.refreshDirs()
                }
                if (!key.reset()) {
                    // Don't have the access to listen on this directory anymore.
                    environment.monitor.raise(ApplicationStopped, this@configureWatchers)
                    break // loop
                }
            }
        }
    } catch (e: Exception) {
        logger.error("Whoops!!", e)
    }
}