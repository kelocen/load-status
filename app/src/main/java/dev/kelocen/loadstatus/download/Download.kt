package dev.kelocen.loadstatus.download

/**
 * A class that defines Download objects.
 */
class Download(
    var url: String = "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip",
    var downloadId: Long = 0,
    var channelID: String = "channelId"
)