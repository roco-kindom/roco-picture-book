package com.lanier.roco.picturebook

import android.app.Application
import com.lanier.roco.picturebook.database.VioletDatabase
import com.lanier.roco.picturebook.manager.DbSyncManager

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        VioletDatabase.init(this)
        DbSyncManager.cachePath = externalCacheDir?.absolutePath?: ""
    }
}