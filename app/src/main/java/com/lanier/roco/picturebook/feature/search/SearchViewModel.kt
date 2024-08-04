package com.lanier.roco.picturebook.feature.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lanier.roco.picturebook.database.VioletDatabase
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.ext.ioWithData
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.ext.main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Mutex

class SearchViewModel : ViewModel() {

    private val limit = 20
    private var page = 1
    private var lock = Mutex()
    private var lastText = ""
    private var isEnd = false

    /**
     * 0 - 全部
     * 1 - 序号
     * 2 - 名称
     * 3 - 组
     * 4 - 属性
     */
    private var status = MutableLiveData<Int>(0)

    val spirits = MutableLiveData<Triple<Int, List<Spirit>, Boolean>>()

    fun setStatus(status: Int) {
        this.status.value = status
        refresh()
        search(lastText)
    }

    fun search(searchText: String) {
        if (searchText.isEmpty()){
            spirits.value = Triple(1, emptyList(), true)
            lastText = ""
            return
        }
        if (lastText != searchText){
            refresh()
        }
        if (lock.isLocked || isEnd) return
        launchSafe {
            lock.lock()
            val list = ioWithData {
                when (status.value){
                    1 -> VioletDatabase.db.spiritDao().getSpiritsSearchByIdPage(searchText, offset = (page - 1) * limit, limit)
                    2 -> VioletDatabase.db.spiritDao().getSpiritsSearchByNamePage(searchText, offset = (page - 1) * limit, limit)
                    3 -> VioletDatabase.db.spiritDao().getSpiritsSearchByGroupIdPage(searchText, offset = (page - 1) * limit, limit)
                    4 -> VioletDatabase.db.spiritDao().getSpiritsSearchByPropertyIdPage(searchText, offset = (page - 1) * limit, limit)
                    else-> VioletDatabase.db.spiritDao().getSpiritsSearchByAllPage(searchText, offset = (page - 1) * limit, limit)
                }
            }
            main {
                isEnd = list.size < limit
                spirits.value = Triple(page, list, isEnd)
                if (isEnd.not()) {
                    page++
                }
            }
            lastText = searchText
            lock.unlock()
        }
    }

    private fun refresh(){
        page = 1
        isEnd = false
    }
}