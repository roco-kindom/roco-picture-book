package com.lanier.roco.picturebook.feature.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lanier.roco.picturebook.database.VioletDatabase
import com.lanier.roco.picturebook.database.entity.Skill
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.ext.ioWithData
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.ext.main
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.manager.DbSyncManager
import com.lanier.roco.picturebook.manager.SyncAction
import com.lanier.roco.picturebook.manager.SyncTask
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import java.util.concurrent.atomic.AtomicBoolean

class MainViewModel : ViewModel() {

    private val limit = 20
    private var page = 1
    private var isLoading = AtomicBoolean(false)

    private var mainLoadJob: Job? = null

    private val _spirits = MutableLiveData<Triple<Int, List<Spirit>, Boolean>>()
    val spirits: LiveData<Triple<Int, List<Spirit>, Boolean>> = _spirits

    private val _skills = MutableLiveData<Triple<Int, List<Skill>, Boolean>>()
    val skills: LiveData<Triple<Int, List<Skill>, Boolean>> = _skills

    private val _syncAction = MutableLiveData<SyncAction>()
    val syncAction: LiveData<SyncAction> = _syncAction

    private val _loadingLiveData = MutableLiveData<Boolean>()
    val loadingLiveData: LiveData<Boolean> = _loadingLiveData

    fun sync(fromServer: Boolean) {
        DbSyncManager.syncData(
            task = buildSyncTask(),
            forceSync = fromServer,
            onStart = {
                _syncAction.value = SyncAction.Loading
                _loadingLiveData.value = true
            },
            onWarning = {},
            onCompleted = { b, t ->
                _syncAction.value = SyncAction.Completed(b, t)
                _loadingLiveData.value = false
            }
        )
    }

    fun load(refresh: Boolean = false) {
        if (isLoading.get()) {
            return
        }
        val oldJob = mainLoadJob
        mainLoadJob = launchSafe {
            oldJob?.cancelAndJoin()
            isLoading.set(true)
            if (refresh) page = 1
            val list = ioWithData {
                val dao = VioletDatabase.db.spiritDao()
                val spirits = dao.getSpiritsByPage(offset = (page - 1) * limit, limit)
                if (spirits.isNotEmpty()) {
                    if (AppData.spiritMaxValidId <= 0) {
                        AppData.spiritMaxValidId = dao.getLatestSpirit().spiritId.toInt()
                    }
                    if (AppData.spiritProperties.isEmpty()) {
                        loadSpiritProperties()
                    }
                    if (AppData.spiritGroups.isEmpty()) {
                        loadSpiritGroups()
                    }
                }
                spirits
            }
            main {
                val isEnd = list.size < limit
                _spirits.value = Triple(page, list, isEnd)
                if (isEnd.not()) {
                    page++
                }
            }
            isLoading.set(false)
            mainLoadJob = null
        }
    }

    private fun loadSpiritGroups() {
        val dao = VioletDatabase.db.spiritDao()
        val groups = dao.getAllEggGroup()
        groups.forEachIndexed { index, spiritGroup ->
            AppData.spiritGroups[index + 1] = spiritGroup
        }
    }

    fun loadSpiritProperties() {
        val dao = VioletDatabase.db.spiritDao()
        val properties = dao.getAllProps()
        properties.forEachIndexed { index, property ->
            AppData.spiritProperties[index + 1] = property
        }
    }

    private fun buildSyncTask() = SyncTask(
        withSpiritConfig = true,
        withSkillConfig = AppData.syncWithSkillConfig,
        withManorSeedConfig = AppData.syncWithManorSeedConfig,
        withSceneConfig = AppData.syncWithSceneConfig,
    )
}