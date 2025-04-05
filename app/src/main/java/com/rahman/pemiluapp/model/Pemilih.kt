package com.rahman.pemiluapp.model

import android.os.Parcel
import android.os.Parcelable

data class Pemilih(
    val nik: String? = "",
    val nama: String? = "",
    val nohp: String? = "",
    val jk: Boolean? = true,
    val tanggal: String? = "",
    val alamat: String? = "",
    val gambar: String? = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readByte() != 0.toByte(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(nik)
        parcel.writeString(nama)
        parcel.writeString(nohp)
        parcel.writeByte(if (jk == true) 1 else 0)
        parcel.writeString(tanggal)
        parcel.writeString(alamat)
        parcel.writeString(gambar)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pemilih> {
        override fun createFromParcel(parcel: Parcel): Pemilih {
            return Pemilih(parcel)
        }

        override fun newArray(size: Int): Array<Pemilih?> {
            return arrayOfNulls(size)
        }
    }
}