package com.lanier.roco.picturebook.feature.search.entity

sealed interface SearchDataType {

    data object Spirit: SearchDataType
    data object Skill: SearchDataType
}