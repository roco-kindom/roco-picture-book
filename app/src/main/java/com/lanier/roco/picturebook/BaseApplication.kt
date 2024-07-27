package com.lanier.roco.picturebook

import android.app.Application
import com.lanier.roco.picturebook.database.VioletDatabase

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        VioletDatabase.init(this)
    }
}