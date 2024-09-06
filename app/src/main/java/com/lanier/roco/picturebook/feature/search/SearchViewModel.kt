package com.lanier.roco.picturebook.feature.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lanier.roco.picturebook.database.VioletDatabase
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.ext.ioWithData
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.ext.main
import com.lanier.roco.picturebook.feature.search.entity.SearchModel
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.manager.SPDelegate
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex

class SearchViewModel : ViewModel() {

    private val limit = 20
    private var page = 1
    private var lock = Mutex()
    private var lastText = ""

    private var spiritSearchModel = SearchModel()

    val spirits = MutableLiveData<Triple<Int, List<Spirit>, Boolean>>()

    /**
     * 模糊查询id
     */
    var fuzzyQueryById = false

    /**
     * 按名称模糊查询
     */
    var fuzzyQueryByName : Boolean = true
        set(value) {
            AppData.SPData.fuzzyQueryByName = value
            field = value
        }
        get() = AppData.SPData.fuzzyQueryByName

    fun modifyProperty(property: Int? = null) {
        val pid = property?.let { sp ->
            if (AppData.spiritProperties.contains(sp)) sp else -1
        } ?: -1
        spiritSearchModel = spiritSearchModel.copy(
            propertyId = pid
        )
    }

    fun modifyGroup(group: Int? = null) {
        val gid = group?.let { sg ->
            if (AppData.spiritGroups.contains(sg)) sg else -1
        } ?: -1
        spiritSearchModel = spiritSearchModel.copy(
            groupId = gid
        )
    }

    fun research() {
        search(lastText)
    }

    fun loadMore() {
        search(lastText, false)
    }

    fun search(input: String, refresh: Boolean = true) {
        launchSafe {
            if (input.isEmpty()) {
                spirits.value = Triple(page, emptyList(), true)
                delay(100)
                return@launchSafe
            }
            if (AppData.spiritMaxValidId <= 0) {
                spirits.value = Triple(page, emptyList(), true)
                delay(100)
                return@launchSafe
            }
            if (lock.isLocked) return@launchSafe
            lock.lock()
            lastText = input
            if (refresh) page = 1
            val gid = if (spiritSearchModel.groupId <= 0) null else spiritSearchModel.groupId.toString()
            val pid = if (spiritSearchModel.propertyId <= 0) null else spiritSearchModel.propertyId.toString()
            val dao = VioletDatabase.db.spiritDao()
            val list = if (input.matches(Regex("\\d+"))) {
                val id = input.toInt()
                if (id in 1..AppData.spiritMaxValidId) {
                    ioWithData { dao.getSpiritsByIdAndOtherFiled(
                        id = input,
                        groupId = gid,
                        propertyId = pid,
                        offset = (page - 1) * limit,
                        limit = limit)
                    }
                } else{
                    emptyList()
                }
            } else {
                ioWithData { dao.getSpiritsByNameAndOtherFiled(
                    name = input,
                    groupId = gid,
                    propertyId = pid,
                    exact = if (fuzzyQueryByName) 0 else 1,
                    offset = (page - 1) * limit,
                    limit = limit)
                }
            }
            println(">>>> 数据 - ${list.size}")
            main {
                val isEnd = list.size < limit
                spirits.value = Triple(page, list, isEnd)
                if (isEnd.not()) page++
            }
            lock.unlock()
        }
    }
}