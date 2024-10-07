package com.lanier.roco.picturebook.feature.main.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.lanier.roco.picturebook.database.VioletDatabase
import com.lanier.roco.picturebook.database.entity.Prop
import com.lanier.roco.picturebook.ext.ioWithData
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.ext.main
import com.lanier.roco.picturebook.feature.main.PropType
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/6 16:40
 */
class PropViewModel : ViewModel() {

    var type: PropType? = null

    var page: Int = 1
        private set
    private val limit = 20

    private val _props = MutableLiveData<Triple<Int, List<Prop>, Boolean>>()
    val props: LiveData<Triple<Int, List<Prop>, Boolean>> = _props
    private val isLoading = AtomicBoolean(false)

    private var getPropJob: Job? = null

    fun load(refresh: Boolean = false) {
        if (isLoading.get()) return
        val oldJob = getPropJob
        getPropJob = launchSafe {
            oldJob?.cancelAndJoin()
            isLoading.set(true)
            if (refresh) page = 1
            val dao = VioletDatabase.db.propDao()
            val list = ioWithData {
                val offset = (page - 1) * limit
                when (type) {
                    PropType.Exp -> {
                        dao.getPropOfExp(offset = offset, limit = limit)
                    }

                    PropType.GuluBall -> {
                        dao.getPropOfGuluBall(offset = offset, limit = limit)
                    }

                    PropType.Medicine -> {
                        dao.getPropOfMedicine(offset = offset, limit = limit)
                    }

                    PropType.SeedsAndCrops -> {
                        dao.getFarmCorrelationExcludeDress(offset = offset, limit = limit)
                    }

                    PropType.SkillStone -> {
                        dao.getPropOfSkillStone(offset = offset, limit = limit)
                    }

                    PropType.TalentAndEndeavor -> {
                        dao.getPropOfTalentAndEndeavor(offset = offset, limit = limit)
                    }

                    null -> { emptyList() }
                }
            }
            main {
                val isEnd = list.size < limit
                _props.value = Triple(page, list, isEnd)
                if (isEnd.not()) {
                    page++
                }
            }
            isLoading.set(false)
            getPropJob = null
        }
    }
}