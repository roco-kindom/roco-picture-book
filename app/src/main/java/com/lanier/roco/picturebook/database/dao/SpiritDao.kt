package com.lanier.roco.picturebook.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import com.lanier.roco.picturebook.database.entity.Equipment
import com.lanier.roco.picturebook.database.entity.Property
import com.lanier.roco.picturebook.database.entity.Skin
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.database.entity.SpiritGroup
import com.lanier.roco.picturebook.database.entity.Talent

@Dao
interface SpiritDao {

    @Insert(entity = Spirit::class, onConflict = REPLACE)
    fun upsertAllSpirits(spirits: List<Spirit>): List<Long>

    @Insert(entity = Property::class, onConflict = REPLACE)
    fun upsertAllProperty(properties: List<Property>): List<Long>

    @Insert(entity = SpiritGroup::class, onConflict = REPLACE)
    fun upsertAllEggGroup(groups: List<SpiritGroup>): List<Long>

    @Insert(entity = Equipment::class, onConflict = REPLACE)
    fun upsertAllEquipments(equipments: List<Equipment>): List<Long>

    @Insert(entity = Talent::class, onConflict = REPLACE)
    fun upsertAllTalents(talents: List<Talent>): List<Long>

    @Insert(entity = Skin::class, onConflict = REPLACE)
    fun upsertAllSkins(skins: List<Skin>): List<Long>

    @Query("select * from spirit where id=:id")
    fun getSpiritById(id: String): Spirit

    @Query("select * from spirit where name like '%'||:name||'%'")
    fun getSpiritsByName(name: String): List<Spirit>

    @Query("select * from spirit where group_id=:groupId")
    fun getSpiritsByGroupId(groupId: String): List<Spirit>

    @Query("select * from spirit where property=:propertyId")
    fun getSpiritsByPropertyId(propertyId: String): List<Spirit>

    @Query("select * from property")
    fun getAllProps(): List<Property>

    @Query("select * from spirit_group")
    fun getAllEggGroup(): List<SpiritGroup>

    @Query("select * from equipment")
    fun getAllEquipments(): List<Equipment>

    @Query("select * from talent")
    fun getAllTalents(): List<Talent>

    @Query("select * from skin")
    fun getAllSkins(): List<Skin>

    @Query("select * from spirit order by cast(id as integer) desc limit :limit offset :offset")
    fun getSpiritsByPage(offset: Int, limit: Int = 20): List<Spirit>

    @Query("select * from spirit where(cast(id as TEXT) like '%'||:searchText||'%' or name like '%'||:searchText||'%' or group_id like '%'||:searchText||'%' or property like '%'||:searchText||'%') order by cast(id as integer) desc limit :limit offset :offset")
    fun getSpiritsSearchByAllPage(searchText: String, offset: Int, limit: Int = 20): List<Spirit>

    @Query("select * from spirit where cast(id as TEXT) like '%'||:searchText||'%' order by cast(id as integer) desc limit :limit offset :offset")
    fun getSpiritsSearchByIdPage(searchText: String, offset: Int, limit: Int = 20): List<Spirit>

    @Query("select * from spirit where name like '%'||:searchText||'%' order by cast(id as integer) desc limit :limit offset :offset")
    fun getSpiritsSearchByNamePage(searchText: String, offset: Int, limit: Int = 20): List<Spirit>

    @Query("select * from spirit where group_id like '%'||:searchText||'%' order by cast(id as integer) desc limit :limit offset :offset")
    fun getSpiritsSearchByGroupIdPage(
        searchText: String,
        offset: Int,
        limit: Int = 20
    ): List<Spirit>

    @Query("select * from spirit where property like '%'||:searchText||'%' order by cast(id as integer) desc limit :limit offset :offset")
    fun getSpiritsSearchByPropertyIdPage(
        searchText: String,
        offset: Int,
        limit: Int = 20
    ): List<Spirit>

    @Query(
        """
        select * from spirit
        where cast(id as TEXT)=:id
        and (:propertyId is null or property like '%'||:propertyId||'%')
        and (:groupId is null or group_id=:groupId)
        order by cast(id as integer) desc limit :limit offset :offset
    """
    )
    fun getSpiritsByIdAndOtherFiled(id: String, propertyId: String?, groupId: String?, offset: Int, limit: Int = 20): List<Spirit>

    @Query(
        """
        select * from spirit
        where (:exact=1 and name=:name) or (:exact=0 and name like '%'||:name||'%')
        and (:propertyId is null or property like '%'||:propertyId||'%')
        and (:groupId is null or group_id=:groupId)
        order by cast(id as integer) desc limit :limit offset :offset
    """
    )
    fun getSpiritsByNameAndOtherFiled(name: String, propertyId: String?, groupId: String?, exact: Int = 0, offset: Int, limit: Int = 20): List<Spirit>

    @Query("select * from spirit order by cast(id as integer) desc limit 1")
    fun getLatestSpirit(): Spirit
}