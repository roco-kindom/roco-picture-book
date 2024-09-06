package com.lanier.roco.picturebook.feature.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lanier.roco.picturebook.database.VioletDatabase
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.ext.ioWithData
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.ext.main
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.manager.DbSyncManager
import com.lanier.roco.picturebook.manager.SyncAction
import kotlinx.coroutines.sync.Mutex

class MainViewModel : ViewModel() {

    private val limit = 20
    private var page = 1
    private var lock = Mutex()

    val spirits = MutableLiveData<Triple<Int, List<Spirit>, Boolean>>()
    val syncAction = MutableLiveData<SyncAction>()

    fun sync(fromServer: Boolean) {
        DbSyncManager.syncData(
            forceSync = fromServer,
            onStart = { syncAction.value = SyncAction.Loading },
            onWarning = {},
            onCompleted = { b, t -> syncAction.value = SyncAction.Completed(b, t) }
        )
    }

    fun load(refresh: Boolean = false) {
        if (lock.isLocked) return
        launchSafe {
            lock.lock()
            if (refresh) {
                page = 1
            }
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
                spirits.value = Triple(page, list, isEnd)
                if (isEnd.not()) {
                    page++
                }
            }
            lock.unlock()
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
}