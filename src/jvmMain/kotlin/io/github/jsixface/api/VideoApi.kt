package io.github.jsixface.api

import io.github.jsixface.common.TrackType
import io.github.jsixface.common.VideoFile
import io.github.jsixface.utils.CodecUtils.parseMediaInfo
import kotlin.io.path.*

typealias VideoList = Map<String, VideoFile>


@OptIn(ExperimentalPathApi::class)
class VideoApi {

    fun getVideos(): VideoList {
        val data = SavedData.load()
        val scan = mutableMapOf<String, VideoFile>()
        data.locations.forEach { l ->
            val loc = Path(l)
            loc.walk(PathWalkOption.FOLLOW_LINKS).forEach { p ->
                if (p.isDirectory().not() && videExtensions.contains(p.extension.lowercase())) {
                    scan[p.pathString] = VideoFile(
                        path = p.pathString,
                        fileName = p.name,
                        modifiedTime = p.getLastModifiedTime().toMillis()
                    )
                }
            }
        }
        val toParse: VideoList = consolidateData(data.details, scan)
        parseMediaFiles(data.details, toParse)
        data.save()
        return data.details
    }

    private fun parseMediaFiles(details: MutableMap<String, VideoFile>, toParse: VideoList) {
        toParse.values.forEach {
            val tracks = parseMediaInfo(it.path)
            details[it.path] = it.copy(
                videos = tracks.filter { t -> t.type == TrackType.Video },
                audios = tracks.filter { t -> t.type == TrackType.Audio },
                subtitles = tracks.filter { t -> t.type == TrackType.Subtitle },
            )
        }
    }

    private fun consolidateData(data: MutableMap<String, VideoFile>, scan: VideoList): VideoList {
        val toScan = scan.filterValues {
            (((data[it.path]?.modifiedTime ?: 0) < it.modifiedTime))
        }
        toScan.forEach { data[it.key] = it.value }
        val toDelete = data.keys.filter { !scan.containsKey(it) }
        toDelete.forEach { data.remove(it) }
        return toScan
    }

    companion object {
        val videExtensions = listOf("avi", "mp4", "mkv", "mpeg4")
    }
}