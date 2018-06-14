package com.orcchg.ecologymap

import android.os.Parcel
import android.os.Parcelable

data class Payload(val descriptionId: Int, val waterDescId: Int,
                   val imageIds: List<Int>, val statusIds: List<Int>) : Parcelable {

    constructor(source: Parcel): this(source.readInt(), source.readInt(), emptyList(), emptyList()) {
        source.readList(imageIds, Int::class.java.classLoader)
        source.readList(statusIds, Int::class.java.classLoader)
    }

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(descriptionId)
        dest.writeInt(waterDescId)
        dest.writeList(imageIds)
        dest.writeList(statusIds)
    }

    companion object {
        @JvmField
        val CREATOR = object : Parcelable.Creator<Payload> {
            override fun createFromParcel(source: Parcel): Payload = Payload(source)

            override fun newArray(size: Int): Array<Payload?> = arrayOfNulls(size)
        }
    }
}
