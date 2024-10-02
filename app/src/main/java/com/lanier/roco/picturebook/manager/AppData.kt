package com.lanier.roco.picturebook.manager

import android.widget.ImageView
import coil.load
import com.lanier.roco.picturebook.database.entity.Property
import com.lanier.roco.picturebook.database.entity.SpiritGroup

object AppData {

    const val propertyIconUrl = "https://res.17roco.qq.com/res/combat/property/"

    const val spiritIconUrl = "https://res.17roco.qq.com/res/combat/icons/"

    /**
     * 同步方式
     */
    var syncType : SyncType = SyncType.CacheFile

    /**
     * 是否同步技能配置
     */
    var syncWithSkillConfig: Boolean = false

    /**
     * 是否同步农场种子配置
     */
    var syncWithManorSeedConfig: Boolean = false

    /**
     * 是否同步场景配置
     */
    var syncWithSceneConfig: Boolean = false

    var spiritMaxValidId = -1
    val spiritProperties = mutableMapOf<Int, Property>()
    val spiritGroups = mutableMapOf<Int, SpiritGroup>()

    fun loadSpiritAvatar(imageView: ImageView, src: String) {
        imageView.load(spiritIconUrl + src)
    }

    fun loadProperty(imageView: ImageView, property: String) {
        imageView.load("$propertyIconUrl$property.png")
    }

    object SPData {

        private const val SP_KEY_SYNC_TIME = "sync_time"
        private const val SP_KEY_FUZZY_QUERY_SPIRIT_BY_NAME = "fuzzy_query_by_name_spirit]"
        private const val SP_KEY_FUZZY_QUERY_SKILL_BY_NAME = "fuzzy_query_by_name_skill"

        var syncTime : Long by SPDelegate(SP_KEY_SYNC_TIME, 0L)
        var fuzzyQuerySpiritByName: Boolean by SPDelegate(SP_KEY_FUZZY_QUERY_SPIRIT_BY_NAME, true)
        var fuzzyQuerySkillByName: Boolean by SPDelegate(SP_KEY_FUZZY_QUERY_SKILL_BY_NAME, true)
    }
}