package com.lanier.roco.picturebook.feature.main.viewmodel

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
import com.lanier.roco.picturebook.manager.sync.DbSyncManager
import com.lanier.roco.picturebook.manager.sync.SyncAction
import com.lanier.roco.picturebook.manager.sync.SyncTask
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.util.concurrent.atomic.AtomicBoolean

class MainViewModel : ViewModel() {

    private val limit = 20
    private var spiritDataPage = 1
    private var skillDataPage = 1
    private var isSpiritDataLoading = AtomicBoolean(false)
    private var isSkillDataLoading = AtomicBoolean(false)

    private var loadSpiritDataJob: Job? = null
    private var loadSkillDataJob: Job? = null

    private val _spirits = MutableLiveData<Triple<Int, List<Spirit>, Boolean>>()
    val spirits: LiveData<Triple<Int, List<Spirit>, Boolean>> = _spirits

    private val _skillsFlow = MutableSharedFlow<Triple<Int, List<Skill>, Boolean>>(
        replay = 5,
    )
    val skillsFlow: Flow<Triple<Int, List<Skill>, Boolean>> = _skillsFlow

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

    fun load() {
        loadSpirits(true)
        loadSkills(true)
    }

    fun loadSpirits(refresh: Boolean = false) {
        if (isSpiritDataLoading.get()) {
            return
        }
        val oldJob = loadSpiritDataJob
        loadSpiritDataJob = launchSafe {
            oldJob?.cancelAndJoin()
            isSpiritDataLoading.set(true)
            if (refresh) spiritDataPage = 1
            val list = ioWithData {
                val dao = VioletDatabase.db.spiritDao()
                val spirits = dao.getSpiritsByPage(offset = (spiritDataPage - 1) * limit, limit)
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
                _spirits.value = Triple(spiritDataPage, list, isEnd)
                if (isEnd.not()) {
                    spiritDataPage++
                }
            }
            isSpiritDataLoading.set(false)
            loadSpiritDataJob = null
        }
    }

    fun loadSkills(refresh: Boolean = false) {
        if (isSkillDataLoading.get()) {
            return
        }
        val oldJob = loadSkillDataJob
        loadSkillDataJob = launchSafe {
            oldJob?.cancelAndJoin()
            isSkillDataLoading.set(true)
            if (refresh) skillDataPage = 1
            val list = ioWithData {
                val dao = VioletDatabase.db.skillDao()
                val skills = dao.getSkillByPage(offset = (skillDataPage - 1) * limit, limit)
                skills
            }
            main {
                val isEnd = list.size < limit
                _skillsFlow.tryEmit(Triple(skillDataPage, list, isEnd))
                if (isEnd.not()) {
                    skillDataPage++
                }
            }
            isSkillDataLoading.set(false)
            loadSkillDataJob = null
        }
    }

    private suspend fun loadSpiritGroups() {
        val dao = VioletDatabase.db.spiritDao()
        val groups = dao.getAllEggGroup()
        groups.forEachIndexed { index, spiritGroup ->
            AppData.spiritGroups[index + 1] = spiritGroup
        }
    }

    private suspend fun loadSpiritProperties() {
        val dao = VioletDatabase.db.spiritDao()
        val properties = dao.getAllProps()
        properties.forEachIndexed { index, property ->
            AppData.spiritProperties[index + 1] = property
        }
    }

    private fun buildSyncTask() = SyncTask(
        withSpiritConfig = true,
        withSkillConfig = AppData.syncWithSkillConfig,
        withPropConfig = AppData.syncWithPropConfig,
        withSceneConfig = AppData.syncWithSceneConfig,
    )
}