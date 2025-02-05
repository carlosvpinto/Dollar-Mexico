package com.carlosvpinto.dollar_mexico.model

import android.os.Parcel
import android.os.Parcelable

data class ApiMexicoResponseItem(
    var buy: Double,
    var date: String,
    var image: String,
    var name: String,
    var sell: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(buy)
        parcel.writeString(date)
        parcel.writeString(image)
        parcel.writeString(name)
        parcel.writeDouble(sell)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ApiMexicoResponseItem> {
        override fun createFromParcel(parcel: Parcel): ApiMexicoResponseItem {
            return ApiMexicoResponseItem(parcel)
        }

        override fun newArray(size: Int): Array<ApiMexicoResponseItem?> {
            return arrayOfNulls(size)
        }
    }
}
