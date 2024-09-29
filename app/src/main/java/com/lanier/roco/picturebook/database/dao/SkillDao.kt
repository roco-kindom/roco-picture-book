package com.lanier.roco.picturebook.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.lanier.roco.picturebook.database.entity.EffectDetails
import com.lanier.roco.picturebook.database.entity.Skill
import com.lanier.roco.picturebook.database.entity.SkillEffect
import com.lanier.roco.picturebook.database.entity.Talent

@Dao
interface SkillDao {

    @Insert(entity = Skill::class, onConflict = OnConflictStrategy.REPLACE)
    fun upsertSkillAll(skills: List<Skill>) : List<Long>

    @Insert(entity = SkillEffect::class, onConflict = OnConflictStrategy.REPLACE)
    fun upsertSkillEffectsAll(skillEffects: List<SkillEffect>) : List<Long>

    @Insert(entity = EffectDetails::class, onConflict = OnConflictStrategy.REPLACE)
    fun upsertEffectDetailsAll(details: List<EffectDetails>) : List<Long>

    @Insert(entity = Talent::class, onConflict = REPLACE)
    fun upsertAllTalents(talents: List<Talent>): List<Long>

    @Query("select * from skill where id=:id")
    fun getSkillById(id: String) : Skill

    @Query("select * from skill where name like '%'||:name||'%'")
    fun getSkillsByName(name: String) : List<Skill>

    @Query("select * from skill_effect")
    fun getSkillEffects() : List<SkillEffect>

    @Query("select * from effect_details")
    fun getSkillEffectDetails() : List<EffectDetails>

    @Query("select * from effect_details where id=:id")
    fun getSkillEffectDetailsById(id: String) : EffectDetails

    @Query("select * from talent")
    fun getAllTalents(): List<Talent>

    /**
     * @param property 与 [skill_effect] 表的 [id] 对应
     */
    @Query("select * from effect_details where property=:property")
    fun getSkillEffectDetailsByProperty(property: String) : EffectDetails
}