package com.lanier.roco.picturebook.manager

import android.widget.ImageView
import coil.load

object AppData {

    const val propertyIconUrl = "https://res.17roco.qq.com/res/combat/property/"

    const val spiritIconUrl = "https://res.17roco.qq.com/res/combat/icons/"

    var syncType : SyncType = SyncType.CacheFile

    fun loadSpiritAvatar(imageView: ImageView, src: String) {
        imageView.load(spiritIconUrl + src)
    }

    fun loadProperty(imageView: ImageView, property: String) {
        imageView.load("$propertyIconUrl$property.png")
    }

    object SPData {

        private const val SP_KEY_SYNC_TIME = "sync_time"

        var syncTime : Long by SPDelegate(SP_KEY_SYNC_TIME, 0L)
    }
}