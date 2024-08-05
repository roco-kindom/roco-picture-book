package com.lanier.roco.picturebook.feature.search.entity

import com.lanier.roco.picturebook.database.entity.SpiritGroup

data class SearchGroup(
    val id: String,
    val name: String
) {

    var selected: Boolean = false

    companion object {

        fun convertByGroup(spiritGroup: SpiritGroup) = SearchGroup(
            id = spiritGroup.id,
            name = spiritGroup.name
        )
    }
}
