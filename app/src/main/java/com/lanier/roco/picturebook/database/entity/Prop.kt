package com.lanier.roco.picturebook.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lanier.roco.picturebook.database.Constant

@Entity(tableName = Constant.TN_PROP)
data class Prop(
    @PrimaryKey val id: String,
    @ColumnInfo val name: String,
    @ColumnInfo val desc: String,
    @ColumnInfo val price: String,
)
