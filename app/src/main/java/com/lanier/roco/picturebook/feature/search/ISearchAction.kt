package com.lanier.roco.picturebook.feature.search

import com.lanier.roco.picturebook.feature.search.entity.SearchDataType

interface ISearchAction {

    fun onResearch(type: SearchDataType)
}