package com.lanier.roco.picturebook.feature.setting

import android.os.Bundle
import android.view.View
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.manager.SyncType

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_settings_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSyncTypePreference()
    }

    private fun initSyncTypePreference() {
        val syncPreference = preferenceScreen
            .findPreference<ListPreference>(getString(R.string.key_sync_type))
        syncPreference?.let {
            it.setOnPreferenceChangeListener { preference, newValue  ->
                if (preference.key == getString(R.string.key_sync_type)) {
                    (newValue as? String)?.let {
                        preference.summary =
                            if (newValue == SyncType.Server.type) {
                                getString(R.string.sync_from_official_server)
                            } else {
                                getString(R.string.sync_from_cache_file)
                            }
                    }
                }
                return@setOnPreferenceChangeListener true
            }
            it.summary =
                if (AppData.syncType == SyncType.Server) {
                    getString(R.string.sync_from_official_server)
                } else {
                    getString(R.string.sync_from_cache_file)
                }
        }
    }
}
