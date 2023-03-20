package io.github.jsixface.api

import io.github.jsixface.logger


class LocationApi {
    private val logger = logger()
    fun getLocations(): List<String> = SavedData.load().locations

    fun addLocation(loc: String) {
        val data = SavedData.load()
        val locations = data.locations
        if (locations.contains(loc).not()) {
            logger.info("Adding $loc to locations")
            locations.add(loc)
            locations.sort()
            data.save()
        }
    }

    fun removeLocation(loc: String) = SavedData.load().let {
        it.locations.remove(loc)
        it.save()
    }
}