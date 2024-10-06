package com.lanier.roco.picturebook.feature.main

/**
 * Desc:
 * Author:  幻弦让叶
 * Date:    2024/10/6 17:01
 */
sealed interface PropType {

    companion object {

        const val TYPE_MEDICINE = 1
        const val TYPE_GULU_BALL = 2
        const val TYPE_EXP = 3
        const val TYPE_TALENT_ENDEAVOR = 4
        const val TYPE_SKILL_STONE = 5
        const val TYPE_SEEDS_CROPS = 6
    }

    val typeValue: Int

    data object Medicine: PropType {
        override val typeValue: Int
            get() = TYPE_MEDICINE
    }

    data object GuluBall: PropType {
        override val typeValue: Int
            get() = TYPE_GULU_BALL
    }

    data object Exp: PropType {
        override val typeValue: Int
            get() = TYPE_EXP
    }

    data object TalentAndEndeavor: PropType {
        override val typeValue: Int
            get() = TYPE_TALENT_ENDEAVOR
    }

    data object SkillStone: PropType {
        override val typeValue: Int
            get() = TYPE_SKILL_STONE
    }

    data object SeedsAndCrops: PropType {
        override val typeValue: Int
            get() = TYPE_SEEDS_CROPS
    }
}