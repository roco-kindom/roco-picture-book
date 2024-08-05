package com.lanier.roco.picturebook.feature.search.entity

import com.lanier.roco.picturebook.database.entity.Property

data class SearchProperty(
    val id: String,
    val name: String,
) {

    var selected: Boolean = false

    companion object {

        fun convertByProperty(property: Property) = SearchProperty(
            id = property.id,
            name = property.name
        )
    }
}
