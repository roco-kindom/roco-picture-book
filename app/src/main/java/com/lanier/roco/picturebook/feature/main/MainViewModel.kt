package com.lanier.roco.picturebook.feature.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lanier.roco.picturebook.database.VioletDatabase
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.ext.ioWithData
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.ext.main
import com.lanier.roco.picturebook.manager.DbSyncManager
import com.lanier.roco.picturebook.manager.SyncAction
import kotlinx.coroutines.sync.Mutex

class MainViewModel : ViewModel() {

    private val limit = 20
    private var page = 1
    private var lock = Mutex()
    private var isEnd = false

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
        if (lock.isLocked || isEnd) return
        launchSafe {
            lock.lock()
            if (refresh) {
                page = 1
                isEnd = false
            }
            val list = ioWithData {
                VioletDatabase.db.spiritDao().getSpiritsByPage(offset = (page - 1) * limit, limit)
            }
            main {
                isEnd = list.size < limit
                spirits.value = Triple(page, list, isEnd)
                if (isEnd.not()) {
                    page++
                }
            }
            lock.unlock()
        }
    }
}