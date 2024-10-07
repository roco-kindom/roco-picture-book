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
    suspend fun upsertSkillAll(skills: List<Skill>): List<Long>

    @Insert(entity = SkillEffect::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSkillEffectsAll(skillEffects: List<SkillEffect>): List<Long>

    @Insert(entity = EffectDetails::class, onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEffectDetailsAll(details: List<EffectDetails>): List<Long>

    @Insert(entity = Talent::class, onConflict = REPLACE)
    suspend fun upsertAllTalents(talents: List<Talent>): List<Long>

    @Query("select * from skill where id=:id")
    suspend fun getSkillById(id: String): Skill

    @Query("select * from skill where name like '%'||:name||'%'")
    suspend fun getSkillsByName(name: String): List<Skill>

    @Query("select * from skill_effect")
    suspend fun getSkillEffects(): List<SkillEffect>

    @Query("select * from effect_details")
    suspend fun getSkillEffectDetails(): List<EffectDetails>

    @Query("select * from effect_details where id=:id")
    suspend fun getSkillEffectDetailsById(id: String): EffectDetails

    @Query("select * from talent")
    suspend fun getAllTalents(): List<Talent>

    /**
     * @param property 与 [skill_effect] 表的 [id] 对应
     */
    @Query("select * from effect_details where property=:property")
    suspend fun getSkillEffectDetailsByProperty(property: String): EffectDetails

    @Query(
        """
            select * from skill order by rowid desc limit :limit offset :offset
        """
    )
    suspend fun getSkillByPage(offset: Int, limit: Int = 20): List<Skill>

    @Query(
        """
            select * from skill
            where (:exact = 1 and name = :name) or (:exact = 0 and name like '%'||:name||'%')
            and (:propertyId is null or property = :propertyId)
            order by rowid desc limit :limit offset :offset
        """
    )
    suspend fun getSkillsByNameAndOtherFiled(
        name: String,
        propertyId: String?,
        exact: Int = 0,
        offset: Int,
        limit: Int = 20
    ): List<Skill>
}