package com.lanier.roco.picturebook.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lanier.roco.picturebook.database.entity.Game

@Dao
interface GameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun upsertAll(games: List<Game>) : List<Long>

    @Query("select * from game")
    fun getAllGames() : List<Game>

    @Query("select * from game where id=:id")
    fun getGameById(id: String) : Game
}