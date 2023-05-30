package io.github.jsixface.api

import io.github.jsixface.common.VideoFile
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

@Serializable
data class SavedData(
    val locations: MutableList<String>,
    val details: MutableMap<String, VideoFile>
) {


    fun save() {
        val dataStr = json.encodeToString(this)
        dataFile.writeText(dataStr)
    }

    companion object {
        fun load(): SavedData {
            if (dataFile.exists().not()) dataFile.createNewFile()
            val dataStr = dataFile.readText()
            return if (dataStr.isNotBlank())
                json.decodeFromString(dataStr)
            else
                SavedData(mutableListOf(), mutableMapOf())
        }
    }
}

private val json = Json {
    ignoreUnknownKeys = true
    prettyPrint = true
}
private const val DATA_FILE_NAME = ".vid-con.json"
private val homeDir = System.getenv()["HOME"] ?: "."
private val dataFile = File(homeDir, DATA_FILE_NAME)

