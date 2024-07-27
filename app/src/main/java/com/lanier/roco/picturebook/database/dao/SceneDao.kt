package com.lanier.roco.picturebook.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lanier.roco.picturebook.database.entity.Scene

@Dao
interface SceneDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(scenes: List<Scene>) : List<Long>

    @Query("select * from scene")
    fun getAllScenes() : List<Scene>

    @Query("select * from scene where id=:id")
    fun getSceneById(id: String) : Scene
}