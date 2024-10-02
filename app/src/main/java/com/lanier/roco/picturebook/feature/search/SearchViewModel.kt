package com.lanier.roco.picturebook.feature.search

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lanier.roco.picturebook.database.VioletDatabase
import com.lanier.roco.picturebook.database.entity.Skill
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.ext.ioWithData
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.ext.main
import com.lanier.roco.picturebook.feature.search.entity.SearchDataType
import com.lanier.roco.picturebook.feature.search.entity.SearchModel
import com.lanier.roco.picturebook.manager.AppData
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicBoolean

class SearchViewModel : ViewModel() {

    private val limit = 20
    private var page = 1
    private var lastText = ""

    private var searchJob: Job? = null
    private var isLoading = AtomicBoolean(false)

    private var spiritSearchModel = SearchModel()

    var searchDataType: SearchDataType? = null

    val spirits = MutableLiveData<Triple<Int, List<Spirit>, Boolean>>()

    val datas = MutableLiveData<Triple<Int, List<Any>, Boolean>>()

    /**
     * 模糊查询id
     */
    var fuzzyQueryById = false

    /**
     * 按名称模糊查询
     */
    var fuzzyQueryByName: Boolean = true
        set(value) {
            when (searchDataType) {
                SearchDataType.Skill -> AppData.SPData.fuzzyQuerySkillByName = value
                SearchDataType.Spirit -> AppData.SPData.fuzzyQuerySpiritByName = value
                null -> {}
            }
            field = value
        }
        get() = when (searchDataType) {
            SearchDataType.Spirit -> AppData.SPData.fuzzyQuerySpiritByName
            SearchDataType.Skill -> AppData.SPData.fuzzyQuerySkillByName
            null -> field
        }

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
        val oldJob = searchJob
        searchJob = launchSafe {
            if (input.isEmpty()) {
                delay(100)
                datas.value = Triple(page, emptyList(), true)
                return@launchSafe
            }
            if (searchDataType is SearchDataType.Spirit) {
                if (AppData.spiritMaxValidId <= 0) {
                    delay(100)
                    datas.value = Triple(page, emptyList(), true)
                    return@launchSafe
                }
            }
            oldJob?.cancelAndJoin()
            isLoading.set(true)
            lastText = input
            if (refresh) page = 1
            val pid =
                if (spiritSearchModel.propertyId <= 0) null else spiritSearchModel.propertyId.toString()
            val list = when (searchDataType) {
                SearchDataType.Skill -> {
                    searchSkill(
                        input = input,
                        pid = pid,
                    )
                }
                
                SearchDataType.Spirit -> {
                    val gid =
                        if (spiritSearchModel.groupId <= 0) null else spiritSearchModel.groupId.toString()
                    searchSpirit(
                        input = input,
                        gid = gid,
                        pid = pid,
                    )
                }

                null -> {
                    emptyList()
                }
            }
            main {
                val isEnd = list.size < limit
                datas.value = Triple(page, list, isEnd)
                if (isEnd.not()) page++
            }
            isLoading.set(false)
            searchJob = null
        }
    }

    private suspend fun searchSpirit(
        input: String,
        gid: String?,
        pid: String?,
    ): List<Spirit> {
        val dao = VioletDatabase.db.spiritDao()
        return if (input.matches(Regex("\\d+"))) {
            val id = input.toInt()
            if (id in 1..AppData.spiritMaxValidId) {
                ioWithData {
                    dao.getSpiritsByIdAndOtherFiled(
                        id = input,
                        groupId = gid,
                        propertyId = pid,
                        offset = (page - 1) * limit,
                        limit = limit
                    )
                }
            } else {
                emptyList()
            }
        } else {
            ioWithData {
                dao.getSpiritsByNameAndOtherFiled(
                    name = input,
                    groupId = gid,
                    propertyId = pid,
                    exact = if (fuzzyQueryByName) 0 else 1,
                    offset = (page - 1) * limit,
                    limit = limit
                )
            }
        }
    }
    
    private suspend fun searchSkill(
        input: String,
        pid: String?,
    ): List<Skill> {
        val dao = VioletDatabase.db.skillDao()
        return ioWithData {
            dao.getSkillsByNameAndOtherFiled(
                name = input,
                propertyId = pid,
                exact = if (fuzzyQueryByName) 0 else 1,
                offset = (page - 1) * limit,
                limit = limit
            )
        }
    }
}