package com.lanier.roco.picturebook.manager

import android.widget.ImageView
import coil.load

object AppData {

    const val propertyIconUrl = "https://res.17roco.qq.com/res/combat/property/"

    const val spiritIconUrl = "https://res.17roco.qq.com/res/combat/icons/"

    fun loadSpiritAvatar(imageView: ImageView, src: String) {
        imageView.load(spiritIconUrl + src)
    }

    fun loadProperty(imageView: ImageView, property: String) {
        imageView.load("$propertyIconUrl$property.png")
    }
}