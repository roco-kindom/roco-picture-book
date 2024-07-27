package com.lanier.roco.picturebook.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.lanier.roco.picturebook.database.Constant

@Entity(tableName = Constant.TN_SEED)
data class Seed(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "harvest_id") val harvestId: String,
    @ColumnInfo val name: String,
    @ColumnInfo val desc: String,
    @ColumnInfo(name = "grown_time") val grownTime: String,
    @ColumnInfo(name = "seed_exp") val seedExp: String,
    @ColumnInfo(name = "harvest_exp") val harvestExpDesc: String,
    @ColumnInfo(name = "harvest_number") val harvestNumber: String,
    @ColumnInfo(name = "buy_lv") val buyLv: String,
    @ColumnInfo val season: String,
    @ColumnInfo(name = "proficiency_type") val proficiencyType: String,
    @ColumnInfo(name = "proficiency_buy_lv") val proficiencyLv: String,
    @ColumnInfo(name = "proficiency_exp") val proficiencyExp: String,
)
