package io.github.jsixface.api

import io.github.jsixface.common.Settings
import io.github.jsixface.logger


class SettingsApi {
    private val logger = logger()

    fun getSettings(): Settings = SavedData.load().settings

    fun saveSettings(settings: Settings) {
        val savedData = SavedData.load()
        val oldSettings = savedData.settings
        logger.info("Replacing old settings $oldSettings with new $settings")
        savedData.copy(settings = settings).save()
    }
}