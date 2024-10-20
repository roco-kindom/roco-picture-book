package com.lanier.roco.picturebook.feature.setting

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.preference.CheckBoxPreference
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.ext.toFormattedString
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.manager.sync.SyncType

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_settings_preferences, rootKey)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSyncTypePreference()
        initSyncTime()
        initSyncChoice()
        initAppVersion()
    }

    private fun initSyncTypePreference() {
        val syncPreference = preferenceScreen
            .findPreference<ListPreference>(getString(R.string.key_sync_type))
        syncPreference?.let {
            it.setOnPreferenceChangeListener { preference, newValue  ->
                if (preference.key == getString(R.string.key_sync_type)) {
                    (newValue as? String)?.let {
                        when (newValue) {
                            SyncType.syncOfServer -> {
                                AppData.syncType = SyncType.Server
                                preference.summary = getString(R.string.sync_from_official_server)
                            }
                            SyncType.syncOfCacheFile -> {
                                AppData.syncType = SyncType.CacheFile
                                preference.summary = getString(R.string.sync_from_cache_file)
                            }
                            else -> {
                                AppData.syncType = SyncType.CacheFile
                                preference.summary = getString(R.string.sync_from_cache_file)
                            }
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

    private fun initSyncTime() {
        val syncTimePreference = preferenceScreen
            .findPreference<Preference>(getString(R.string.key_last_sync_time))
        syncTimePreference?.let {
            val lastTime = AppData.SPData.syncTime
            it.summary = if (lastTime <= 0L) "--" else lastTime.toFormattedString("yyyy/MM/dd HH:mm:ss")
        }
    }

    private fun initSyncChoice() {
        val choiceOfSkillConfig = preferenceScreen
            .findPreference<CheckBoxPreference>(getString(R.string.key_sync_skill))
        choiceOfSkillConfig?.let {
            it.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    AppData.syncWithSkillConfig = (newValue as? Boolean) ?: false
                    true
                }
        }

        val choiceOfPropConfig = preferenceScreen
            .findPreference<CheckBoxPreference>(getString(R.string.key_sync_props))
        choiceOfPropConfig?.let {
            it.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    AppData.syncWithPropConfig = (newValue as? Boolean) ?: false
                    true
                }
        }

        val choiceOfScenesConfig = preferenceScreen
            .findPreference<CheckBoxPreference>(getString(R.string.key_sync_scene))
        choiceOfScenesConfig?.let {
            it.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { preference, newValue ->
                    AppData.syncWithSceneConfig = (newValue as? Boolean) ?: false
                    true
                }
        }
    }

    private fun initAppVersion() {
        val versionPreference = preferenceScreen
            .findPreference<Preference>(getString(R.string.key_sundry_version))
        versionPreference?.let {
            context?.let { mContext ->
                it.summary = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    mContext.packageManager.getPackageInfo(mContext.packageName, PackageManager.PackageInfoFlags.of(0)).versionName
                } else {
                    mContext.packageManager.getPackageInfo(mContext.packageName, 0).versionName
                }
            }
        }
    }
}
