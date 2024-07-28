package com.lanier.roco.picturebook.feature.setting

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.lanier.roco.picturebook.R

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_settings_preferences, rootKey)
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
        return false
    }
}
