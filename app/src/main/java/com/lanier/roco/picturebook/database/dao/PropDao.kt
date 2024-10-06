package com.lanier.roco.picturebook.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lanier.roco.picturebook.database.entity.Prop
import com.lanier.roco.picturebook.database.entity.Seed

@Dao
interface PropDao {

    @Insert(entity = Prop::class, onConflict = OnConflictStrategy.REPLACE)
    fun upsertAllProps(prop: List<Prop>) : List<Long>

    @Insert(entity = Seed::class, onConflict = OnConflictStrategy.REPLACE)
    fun upsertAllSeeds(seeds: List<Seed>) : List<Long>

    @Query("select * from prop where id=:id")
    fun getPropById(id: String) : Prop

    @Query("select * from seed")
    fun getAllSeeds() : List<Seed>

    /**
     * 查找药剂道具
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '1684%' or id like '169%')
        order by rowid limit :limit offset :offset
    """)
    fun getPropOfMedicine(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找咕噜球
     *
     * 国王球id为 [17301507]
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '1703%' or id = '17301507')
        order by rowid limit :limit offset :offset
    """)
    fun getPropOfGuluBall(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找经验果
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '1717%' or id like '1723%' or id like '17498%')
        order by rowid limit :limit offset :offset
    """)
    fun getPropOfExp(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找天赋糖
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '1736%')
        order by rowid limit :limit offset :offset
    """)
    fun getPropOfTalent(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找努力果
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '1756%' or id like '1762%')
        order by rowid limit :limit offset :offset
    """)
    fun getPropOfEndeavor(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找技能石
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '17432%')
        order by rowid limit :limit offset :offset
    """)
    fun getPropOfSkillStone(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找服装 - 头部
     * 33619969 - 起始id
     * 33620406 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3361%' or id like '3362%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfHead(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找服装 - 面部
     * 33685505 - 起始id
     * 33685543 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3368%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfFace(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找服装 - 身体
     * 33751041 - 起始id
     * 33751470 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3375%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfBody(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找服装 - 身体
     * 33816577 - 起始id
     * 33816980 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3381%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfShoe(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找服装 - 背部
     * 33882113 - 起始id
     * 33882265 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3388%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfBack(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找服装 - 左手手持
     * 33947649 - 起始id
     * 33947812 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3394%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressLeftHand(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找服装 - 右手手持
     * 34013185 - 起始id
     * 34013524 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3401%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressRightHand(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找表情
     * 34078721 - 起始id
     * 34078734 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3407%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfExpression(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找肤色
     * 34144257 - 起始id
     * 34144266 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3414%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfSkin(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找法阵
     * 34209794 - 起始id
     * 34209833 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3420%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfRingLightSpell(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找魔法装扮
     * 34275330 - 起始id
     * 34275422 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3427%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfMagic(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找脚印
     * 34340866 - 起始id
     * 34340897 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3434%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfFootprint(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找铭牌
     * 34406402 - 起始id
     * 34406445 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3440%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfNameplate(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找冒泡框
     * 34471938 - 起始id
     * 34471948 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '3447%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfBubbleBox(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找变形魔法
     * 50397187 - 起始id
     * 50397300 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '5039%')
        order by rowid limit :limit offset :offset
    """)
    fun getPropOfShapeMagic(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找任务道具
     * 67174402 - 起始id
     * 67174481 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '6717%')
        order by rowid limit :limit offset :offset
    """)
    fun getPropOfTask1(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找日常道具
     */
    @Query("""
        select * from prop
        where length(id) = 8 and (id like '6723%' or id like '6730%' or id like '6737%')
        order by rowid limit :limit offset :offset
    """)
    fun getPropOfDaily1(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找农场种子
     *
     * 100728835 - 起始id
     * 100728965 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 9 and (id like '10072%')
        order by rowid limit :limit offset :offset
    """)
    fun getFarmSeeds(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找农场作物
     *
     * 100794370 - 起始id
     * 100794510 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 9 and (id like '10079%')
        order by rowid limit :limit offset :offset
    """)
    fun getFarmCrop(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找农场肥料
     *
     * 100859906 - 起始id
     * 100859909 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 9 and (id like '10085%')
        order by rowid limit :limit offset :offset
    """)
    fun getFarmFertilizer(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找农场装扮
     *
     * 稻草人
     * 100925442 - 100925469
     *
     * 小屋
     * 100990978 - 100991012
     *
     * 遮阳伞
     * 101056514 - 101056548
     *
     * 草堆
     * 101122050 - 101122084
     *
     * 告示牌
     * 101187586 - 101187620
     */
    @Query("""
        select * from prop
        where length(id) = 9 and 
        (id like '10092%' or id like '10099%' or id like '10105%' or id like '10112%' or id like '10118%')
        order by rowid limit :limit offset :offset
    """)
    fun getFarmDress(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找农场收获道具
     *
     * 117506051 - 起始id
     * 117506059 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 9 and (id like '11750%')
        order by rowid limit :limit offset :offset
    """)
    fun getPropOfHarvest(
        offset: Int,
        limit: Int = 20
    )

    /**
     * 查找魔法装扮2
     * 34275330 - 起始id
     * 34275422 - 结束id
     */
    @Query("""
        select * from prop
        where length(id) = 9 and (id like '15171%')
        order by rowid limit :limit offset :offset
    """)
    fun getDressOfMagic2(
        offset: Int,
        limit: Int = 20
    )
}