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
    suspend fun upsertAllSpirits(spirits: List<Spirit>): List<Long>

    @Insert(entity = Property::class, onConflict = REPLACE)
    suspend fun upsertAllProperty(properties: List<Property>): List<Long>

    @Insert(entity = SpiritGroup::class, onConflict = REPLACE)
    suspend fun upsertAllEggGroup(groups: List<SpiritGroup>): List<Long>

    @Insert(entity = Equipment::class, onConflict = REPLACE)
    suspend fun upsertAllEquipments(equipments: List<Equipment>): List<Long>

    @Insert(entity = Skin::class, onConflict = REPLACE)
    suspend fun upsertAllSkins(skins: List<Skin>): List<Long>

    @Query("select * from spirit where spirit_id=:id")
    suspend fun getSpiritById(id: String): Spirit

    @Query("select * from spirit where name like '%'||:name||'%'")
    suspend fun getSpiritsByName(name: String): List<Spirit>

    @Query("select * from spirit where group_id=:groupId")
    suspend fun getSpiritsByGroupId(groupId: String): List<Spirit>

    @Query("select * from spirit where property=:propertyId")
    suspend fun getSpiritsByPropertyId(propertyId: String): List<Spirit>

    @Query("select * from property")
    suspend fun getAllProps(): List<Property>

    @Query("select * from spirit_group")
    suspend fun getAllEggGroup(): List<SpiritGroup>

    @Query("select * from equipment")
    suspend fun getAllEquipments(): List<Equipment>

    @Query("select * from skin")
    suspend fun getAllSkins(): List<Skin>

//    @Query("select * from spirit order by cast(id as integer) desc limit :limit offset :offset")
//    suspend fun getSpiritsByPage(offset: Int, limit: Int = 20): List<Spirit>

    @Query("select * from spirit order by rowid desc limit :limit offset :offset")
    suspend fun getSpiritsByPage(offset: Int, limit: Int = 20): List<Spirit>

    @Deprecated("useless")
    @Query("select * from spirit where(cast(spirit_id as TEXT) like '%'||:searchText||'%' or name like '%'||:searchText||'%' or group_id like '%'||:searchText||'%' or property like '%'||:searchText||'%') order by cast(spirit_id as integer) desc limit :limit offset :offset")
    suspend fun getSpiritsSearchByAllPage(searchText: String, offset: Int, limit: Int = 20): List<Spirit>

    @Deprecated("useless")
    @Query("select * from spirit where cast(spirit_id as TEXT) like '%'||:searchText||'%' order by cast(spirit_id as integer) desc limit :limit offset :offset")
    suspend fun getSpiritsSearchByIdPage(searchText: String, offset: Int, limit: Int = 20): List<Spirit>

    @Deprecated("useless")
    @Query("select * from spirit where name like '%'||:searchText||'%' order by cast(spirit_id as integer) desc limit :limit offset :offset")
    suspend fun getSpiritsSearchByNamePage(searchText: String, offset: Int, limit: Int = 20): List<Spirit>

    @Deprecated("useless")
    @Query("select * from spirit where group_id like '%'||:searchText||'%' order by cast(spirit_id as integer) desc limit :limit offset :offset")
    suspend fun getSpiritsSearchByGroupIdPage(
        searchText: String,
        offset: Int,
        limit: Int = 20
    ): List<Spirit>

    @Deprecated("useless")
    @Query("select * from spirit where property like '%'||:searchText||'%' order by cast(spirit_id as integer) desc limit :limit offset :offset")
    suspend fun getSpiritsSearchByPropertyIdPage(
        searchText: String,
        offset: Int,
        limit: Int = 20
    ): List<Spirit>

    @Query(
        """
        select * from spirit
        where cast(spirit_id as TEXT)=:id
        and (:propertyId is null or property like '%'||:propertyId||'%')
        and (:groupId is null or group_id=:groupId)
        order by rowid desc limit :limit offset :offset
    """
    )
    suspend fun getSpiritsByIdAndOtherFiled(id: String, propertyId: String?, groupId: String?, offset: Int, limit: Int = 20): List<Spirit>

    @Query(
        """
        select * from spirit
        where (:exact=1 and name=:name) or (:exact=0 and name like '%'||:name||'%')
        and (:propertyId is null or property like '%'||:propertyId||'%')
        and (:groupId is null or group_id=:groupId)
        order by rowid desc limit :limit offset :offset
    """
    )
    suspend fun getSpiritsByNameAndOtherFiled(name: String, propertyId: String?, groupId: String?, exact: Int = 0, offset: Int, limit: Int = 20): List<Spirit>

    @Query("select * from spirit order by cast(spirit_id as integer) desc limit 1")
    suspend fun getLatestSpirit(): Spirit
}