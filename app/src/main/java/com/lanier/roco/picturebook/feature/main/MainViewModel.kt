package com.lanier.roco.picturebook.feature.main

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lanier.roco.picturebook.database.VioletDatabase
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.ext.ioWithData
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.ext.main
import kotlinx.coroutines.sync.Mutex

class MainViewModel : ViewModel() {

    private val limit = 20
    private var page = 1
    private var lock = Mutex()

    val spirits = MutableLiveData<Triple<Int, List<Spirit>, Boolean>>()

    fun load() {
        if (lock.isLocked) return
        launchSafe {
            lock.lock()
            val list = ioWithData {
                VioletDatabase.db.spiritDao().getSpiritsByPage(offset = (page - 1) * limit, limit)
            }
            main {
                spirits.value = Triple(page, list, list.size < limit)
                page ++
            }
            lock.unlock()
        }
    }
}