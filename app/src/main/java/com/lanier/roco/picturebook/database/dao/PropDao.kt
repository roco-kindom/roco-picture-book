package com.lanier.roco.picturebook.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lanier.roco.picturebook.database.entity.Prop
import com.lanier.roco.picturebook.database.entity.Seed

@Dao
interface PropDao {

    @Insert(entity = Prop::class, onConflict = OnConflictStrategy.REPLACE)
    fun upsertAllProps(prop: List<Prop>) : List<Long>

    @Insert(entity = Seed::class, onConflict = OnConflictStrategy.REPLACE)
    fun upsertAllSeeds(seeds: List<Seed>) : List<Long>

    @Query("select * from prop where id=:id")
    fun getPropById(id: String) : Prop

    @Query("select * from seed")
    fun getAllSeeds() : List<Seed>
}