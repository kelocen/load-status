package dev.kelocen.loadstatus.download

import android.os.Parcel
import android.os.Parcelable
import dev.kelocen.loadstatus.receiver.DownloadReceiver

/**
 * A class that defines download objects used by [DownloadReceiver].
 */
class ReceiverDownload(
        var name: String? = "name",
        private var url: String? = "url",
        var downloadId: Long = 0,
        private var channelID: String? = "channelId",
        var sizeKb: Double = 0.0,
        var status: String? = "status",
        var date: String? = "0/00/0000",
) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readLong(),
            parcel.readString(),
            parcel.readDouble(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(url)
        parcel.writeLong(downloadId)
        parcel.writeString(channelID)
        parcel.writeDouble(sizeKb)
        parcel.writeString(status)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ReceiverDownload> {
        override fun createFromParcel(parcel: Parcel): ReceiverDownload {
            return ReceiverDownload(parcel)
        }

        override fun newArray(size: Int): Array<ReceiverDownload?> {
            return arrayOfNulls(size)
        }
    }
}