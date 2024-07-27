package com.lanier.roco.picturebook.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lanier.roco.picturebook.database.Constant

@Entity(tableName = Constant.TN_GAME)
data class Game(
    @PrimaryKey val id: String,
    @ColumnInfo val name: String,
    @ColumnInfo val tips: String,
    @ColumnInfo val type: String,
    @ColumnInfo(name = "scene_id") val sceneId: String,
)