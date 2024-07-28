package com.lanier.roco.picturebook.feature.setting

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.lanier.roco.picturebook.R

class SettingsActivity : AppCompatActivity() {

    private val viewmodel by viewModels<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
    }
}