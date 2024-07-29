package com.lanier.roco.picturebook.manager

sealed interface SyncType {

    data object Server : SyncType
    data object CacheFile : SyncType
}