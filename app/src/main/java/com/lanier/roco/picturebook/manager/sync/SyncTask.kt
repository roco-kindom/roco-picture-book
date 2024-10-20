package com.lanier.roco.picturebook.manager.sync

/**
 * Created by 幻弦让叶
 * on 2024/9/29, at 13:29
 *
 */
data class SyncTask(
    val withSpiritConfig: Boolean = true,
    val withSkillConfig: Boolean,
    val withPropConfig: Boolean,
    val withSceneConfig: Boolean,
)