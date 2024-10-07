package com.lanier.roco.picturebook.manager.sync

sealed class SyncType(val type: String) {

    companion object {
        const val syncOfServer = "1"
        const val syncOfCacheFile = "2"
    }

    data object Server : SyncType(syncOfServer)
    data object CacheFile : SyncType(syncOfCacheFile)
}