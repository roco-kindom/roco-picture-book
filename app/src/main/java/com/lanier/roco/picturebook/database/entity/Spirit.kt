package com.lanier.roco.picturebook.database.entity

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lanier.roco.picturebook.database.Constant

@Entity(tableName = Constant.TN_SPIRIT)
data class Spirit(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "spirit_id") val spiritId: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "icon_src") val iconSrc: String,
    @ColumnInfo(name = "interest") val interest: String,
    @ColumnInfo(name = "color") val color: String,
    @ColumnInfo(name = "height") val height: String,
    @ColumnInfo(name = "weight") val weight: String,
    @ColumnInfo(name = "group_id") val group: String,
    @ColumnInfo(name = "first_id") val firstID: String,
    @ColumnInfo(name = "get_from") val getForm: String,
    @ColumnInfo(name = "description") val description: String,

    @ColumnInfo(name = "hp") val sm: String,
    @ColumnInfo(name = "attack") val wg: String,
    @ColumnInfo(name = "defence") val fy: String,
    @ColumnInfo(name = "magic_attack") val mg: String,
    @ColumnInfo(name = "magic_defence") val mk: String,
    @ColumnInfo(name = "speed") val sd: String,

    @ColumnInfo(name = "evolution_form_id") val evolutionFormID: String,
    @ColumnInfo(name = "evolution_to_ids") val evolutionToIDs: String,

    @ColumnInfo(name = "endTime") val endTime: String,
    @ColumnInfo(name = "expType") val expType: String,
    @ColumnInfo(name = "property") val property: String,
    @ColumnInfo(name = "habitat") val habitat: String,
    @ColumnInfo(name = "is_in_book") val isInBook: String,
    @ColumnInfo(name = "m_type") val mType: String,
    @ColumnInfo(name = "m_speed") val mspeed: String,
    @ColumnInfo(name = "preview_src") val previewSrc: String,
    @ColumnInfo(name = "propo_level") val propoLevel: String,
    @ColumnInfo(name = "skin_num") val skinNum: String,
    @ColumnInfo(name = "src") val src: String,
    @ColumnInfo(name = "state") val state: String,
    @ColumnInfo(name = "catch_rate") val catchRate: String,
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(spiritId)
        parcel.writeString(name)
        parcel.writeString(iconSrc)
        parcel.writeString(interest)
        parcel.writeString(color)
        parcel.writeString(height)
        parcel.writeString(weight)
        parcel.writeString(group)
        parcel.writeString(firstID)
        parcel.writeString(getForm)
        parcel.writeString(description)
        parcel.writeString(sm)
        parcel.writeString(wg)
        parcel.writeString(fy)
        parcel.writeString(mg)
        parcel.writeString(mk)
        parcel.writeString(sd)
        parcel.writeString(evolutionFormID)
        parcel.writeString(evolutionToIDs)
        parcel.writeString(endTime)
        parcel.writeString(expType)
        parcel.writeString(property)
        parcel.writeString(habitat)
        parcel.writeString(isInBook)
        parcel.writeString(mType)
        parcel.writeString(mspeed)
        parcel.writeString(previewSrc)
        parcel.writeString(propoLevel)
        parcel.writeString(skinNum)
        parcel.writeString(src)
        parcel.writeString(state)
        parcel.writeString(catchRate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Spirit> {
        override fun createFromParcel(parcel: Parcel): Spirit {
            return Spirit(parcel)
        }

        override fun newArray(size: Int): Array<Spirit?> {
            return arrayOfNulls(size)
        }
    }

}
