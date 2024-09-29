package com.lanier.roco.picturebook.manager

/**
 * Created by huanggua (幻弦让叶)
 * on 2024/9/29, at 13:29
 *
 *
 */
data class SyncTask(
    val withSpiritConfig: Boolean = true,
    val withSkillConfig: Boolean,
    val withManorSeedConfig: Boolean,
    val withSceneConfig: Boolean,
)