package com.lanier.roco.picturebook

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import com.lanier.roco.picturebook.database.VioletDatabase
import com.lanier.roco.picturebook.manager.sync.DbSyncManager
import com.lanier.roco.picturebook.manager.SPManager

class BaseApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        VioletDatabase.init(this)
        DbSyncManager.cachePath = externalCacheDir?.absolutePath?: ""
        SPManager.init(this)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .respectCacheHeaders(false)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("img_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .build()
    }
}