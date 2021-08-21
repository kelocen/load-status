package dev.kelocen.loadstatus.download

/**
 * A class that defines Download objects.
 */
class Download(
        var url: String? = null,
        var downloadId: Long = 0,
        var channelID: String = "channelId",
)