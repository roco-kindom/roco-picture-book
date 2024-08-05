package com.lanier.roco.picturebook.feature.search.entity

data class SearchModel(
    val propertyId: Int = -1, // 多选过滤: 属性
    val groupId: Int = -1, // 过滤: 组别
)
