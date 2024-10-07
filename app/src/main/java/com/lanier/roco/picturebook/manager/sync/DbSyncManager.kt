package com.lanier.roco.picturebook.manager.sync

import android.os.Build
import com.lanier.roco.picturebook.database.VioletDatabase
import com.lanier.roco.picturebook.database.entity.Prop
import com.lanier.roco.picturebook.database.entity.Property
import com.lanier.roco.picturebook.database.entity.Skill
import com.lanier.roco.picturebook.database.entity.Skin
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.database.entity.SpiritGroup
import com.lanier.roco.picturebook.database.entity.Talent
import com.lanier.roco.picturebook.ext.launchSafe
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.Inflater

object DbSyncManager {

    const val DATABASE_URL = "https://res.17roco.qq.com/conf/Angel.config"
    private const val UA =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6261.95 Safari/537.36"

    private val cacheFilename = "Angel.config"
    var cachePath = ""
        set(value) {
            field = if (value.isEmpty()) {
                ""
            } else {
                if (value.last() == File.separatorChar) {
                    "$value$cacheFilename"
                } else {
                    "$value${File.separator}$cacheFilename"
                }
            }
        }

    private val scope = MainScope()

    fun syncData(
        forceSync: Boolean,
        task: SyncTask,
        onStart: () -> Unit,
        onWarning: (Throwable) -> Unit,
        onCompleted: (Boolean, Throwable?) -> Unit
    ) {
        if (cachePath.isEmpty()) {
            onWarning.invoke(Throwable("缓存路径为空"))
        }
        scope.launchSafe {
            if (forceSync) {
                syncFromServer(
                    task = task,
                    onStart = onStart,
                    onCompleted = { s, t ->
                        withContext(Dispatchers.Main) {
                            onCompleted.invoke(
                                s,
                                t
                            )
                        }
                    },
                )
            } else {
                syncFromCache(
                    task = task,
                    onStart = onStart,
                    onCompleted = { s, t ->
                        withContext(Dispatchers.Main) {
                            onCompleted.invoke(
                                s,
                                t
                            )
                        }
                    },
                )
            }
        }
    }

    private suspend fun syncFromServer(
        task: SyncTask,
        onStart: () -> Unit,
        onCompleted: suspend (Boolean, Throwable?) -> Unit
    ) {
        onStart.invoke()
        withContext(Dispatchers.IO) {
            val url = URL(DATABASE_URL)
            val connection = url.openConnection() as HttpURLConnection
            connection.setRequestProperty("User-Agent", UA)
            try {
                connection.requestMethod = "GET"
                withContext(Dispatchers.IO) {
                    connection.connect()
                }
                val responseCode = connection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val inputStream = connection.inputStream
                    decompressAndSaveToLocalCache(
                        task = task,
                        inputStream = inputStream,
                        onCompleted = onCompleted
                    )
                } else {
                    val responseMessage = connection.responseMessage
                    onCompleted.invoke(
                        false,
                        Throwable("网络错误 : $responseCode $responseMessage")
                    )
                }
            } catch (e: Exception) {
                onCompleted.invoke(false, Throwable("错误: ${e.message}"))
            } finally {
                connection.disconnect()
            }
        }
    }

    private suspend fun syncFromCache(
        task: SyncTask,
        onStart: () -> Unit,
        onCompleted: suspend (Boolean, Throwable?) -> Unit
    ) {
        onStart.invoke()
        if (cachePath.isEmpty()) {
            onCompleted.invoke(false, Throwable("无法从缓存文件同步, 因为缓存文件路径为空"))
            return
        }
        val file = File(cachePath)
        if (file.exists().not()) {
            onCompleted.invoke(false, Throwable("无法从缓存文件同步, 因为没有缓存文件"))
            return
        }
        withContext(Dispatchers.IO) {
            val content = file.readText()
            parseContent(task = task, content = content)
        }
        onCompleted.invoke(true, null)
    }

    private suspend fun decompressAndSaveToLocalCache(
        task: SyncTask,
        inputStream: InputStream,
        onCompleted: suspend (Boolean, Throwable?) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            val zlibOffset = 7 // 指定起始字节偏移
            val dataBytes = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                withContext(Dispatchers.IO) {
                    inputStream.readAllBytes()
                }
            } else {
                inputStream.readBytes()
            }
            try {
                val zlibData = dataBytes.sliceArray(zlibOffset until dataBytes.size)
                val inflater = Inflater()
                inflater.setInput(zlibData)
                val buffer = ByteArray(1024)
                val outputStream = ByteArrayOutputStream()
                try {
                    while (!inflater.finished()) {
                        val count = inflater.inflate(buffer)
                        outputStream.write(buffer, 0, count)
                    }
                    inflater.end()
                    val decompressedData = outputStream.toByteArray()
                    withContext(Dispatchers.IO) {
                        FileOutputStream(cachePath).use { fos ->
                            fos.write(decompressedData)
                        }
                        parseStream(task, outputStream)
                    }
                    onCompleted.invoke(true, null)
                } catch (e: Exception) {
                    onCompleted.invoke(false, Throwable("解压缩失败: ${e.message}"))
                } finally {
                    withContext(Dispatchers.IO) {
                        outputStream.close()
                    }
                }
            } catch (e: Exception) {
                onCompleted.invoke(false, Throwable("文件读取失败: ${e.message}"))
            }
        }
    }

    private suspend fun parseStream(task: SyncTask, outputStream: ByteArrayOutputStream) {
        val byteArray = outputStream.toByteArray()
        val content = byteArray.toString(Charsets.UTF_8)
        parseContent(task, content)
    }

    private suspend fun parseContent(task: SyncTask, content: String) {

        val spiritConfigRegex =
            """<SpiritConfig.*?>(.*?)</SpiritConfig>""".toRegex(RegexOption.DOT_MATCHES_ALL)
        val spiritConfigContent = spiritConfigRegex.find(content)?.groups?.get(1)?.value ?: ""

        val skillConfigRegex =
            """<SpiritSkillConfig.*?>(.*?)</SpiritSkillConfig>""".toRegex(RegexOption.DOT_MATCHES_ALL)
        val skillConfigContent = skillConfigRegex.find(content)?.groups?.get(1)?.value ?: ""

        val propConfigRegex =
            """<ItemConfig[^>]*>\s*<Items>(.*?)</Items>\s*</ItemConfig>""".toRegex(RegexOption.DOT_MATCHES_ALL)
        val propConfigContent = propConfigRegex.find(content)?.groups?.get(1)?.value ?: ""

        val nodeRegex = """<(\w+)\s+(.*?)/>""".toRegex()
        // 这里只匹配<Item>节点, 节点内的数据由内部自己处理
        val itemRegex = """<Item>(.*?)</Item>""".toRegex(RegexOption.DOT_MATCHES_ALL)

        withContext(Dispatchers.Default) {
            val deferredList = mutableListOf<Deferred<Unit>>()

            if (task.withSpiritConfig && spiritConfigContent.isNotBlank()) {
                deferredList.add(
                    async { processSpiritConfig(nodeRegex, spiritConfigContent) }
                )
            }
            if (task.withSkillConfig && skillConfigContent.isNotBlank()) {
                deferredList.add(
                    async { processSkillConfig(nodeRegex, skillConfigContent) }
                )
            }
            if (task.withPropConfig && propConfigContent.isNotBlank()) {
                deferredList.add(
                    async { processPropConfig(itemRegex, propConfigContent) }
                )
            }

            deferredList.awaitAll()
        }
    }

    private suspend fun processSpiritConfig(nodeRegex: Regex, spiritConfigContent: String) {
        val propertyDesList = mutableListOf<Property>()
        val groupTypeDesList = mutableListOf<SpiritGroup>()
        val spiritSkinDesList = mutableListOf<Skin>()
        val spiritDesList = mutableListOf<Spirit>()
        withContext(Dispatchers.Default) {
            nodeRegex.findAll(spiritConfigContent).forEachIndexed { index, matchResult ->
                val nodeName = matchResult.groups[1]?.value ?: ""
                val attributes = matchResult.groups[2]?.value ?: ""

                val keyValueRegex = """(\w+)="([^"]*)"""".toRegex()
                val attributeMap = mutableMapOf<String, String>()
                keyValueRegex.findAll(attributes).forEach { result ->
                    val key = result.groups[1]?.value ?: ""
                    val value = result.groups[2]?.value ?: ""
                    attributeMap[key] = value
                }
                when (nodeName) {
                    "PropertyDes" -> {
                        val id = attributeMap["id"] ?: ""
                        val name = attributeMap["name"] ?: ""
                        propertyDesList.add(Property(id, name))
                    }

                    "groupTypeDes" -> {
                        val id = attributeMap["id"] ?: ""
                        val name = attributeMap["name"] ?: ""
                        groupTypeDesList.add(SpiritGroup(id, name))
                    }

                    "spiritSkinDes" -> {
                        val id = attributeMap["id"] ?: ""
                        val name = attributeMap["name"] ?: ""
                        val description = attributeMap["description"] ?: ""
                        val getForm = attributeMap["getForm"] ?: ""
                        val quality = attributeMap["quality"] ?: ""
                        spiritSkinDesList.add(Skin(id, name, quality))
                    }

                    "SpiritDes" -> {
                        val id = attributeMap["id"] ?: ""
                        if (id.toInt() < 10000) {
                            spiritDesList.add(
                                Spirit(
                                    id = attributeMap["id"] ?: "",
                                    spiritId = attributeMap["id"] ?: "",
                                    name = attributeMap["name"] ?: "",
                                    iconSrc = attributeMap["iconSrc"] ?: "",
                                    interest = attributeMap["interest"] ?: "",
                                    color = attributeMap["color"] ?: "",
                                    height = attributeMap["height"] ?: "",
                                    weight = attributeMap["weight"] ?: "",
                                    group = attributeMap["group"] ?: "",
                                    firstID = attributeMap["firstID"] ?: "",
                                    getForm = attributeMap["getForm"] ?: "",
                                    description = attributeMap["description"] ?: "",
                                    sm = attributeMap["sm"] ?: "",
                                    wg = attributeMap["wg"] ?: "",
                                    fy = attributeMap["fy"] ?: "",
                                    mg = attributeMap["mg"] ?: "",
                                    mk = attributeMap["mk"] ?: "",
                                    sd = attributeMap["sd"] ?: "",
                                    evolutionFormID = attributeMap["EvolutionFormID"] ?: "",
                                    evolutionToIDs = attributeMap["EvolutiontoIDs"] ?: "",
                                    endTime = attributeMap["endTime"] ?: "",
                                    expType = attributeMap["expType"] ?: "",
                                    property = attributeMap["features"] ?: "",
                                    habitat = attributeMap["habitat"] ?: "",
                                    isInBook = attributeMap["isInBook"] ?: "",
                                    mType = attributeMap["Mtype"] ?: "",
                                    mspeed = attributeMap["mspeed"] ?: "",
                                    previewSrc = attributeMap["previewSrc"] ?: "",
                                    propoLevel = attributeMap["propoLevel"] ?: "",
                                    skinNum = attributeMap["skinnum"] ?: "",
                                    src = attributeMap["src"] ?: "",
                                    state = attributeMap["state"] ?: "",
                                    catchRate = attributeMap["catchrate"] ?: ""
                                )
                            )
                        }
                    }
                }
            }
        }

        withContext(Dispatchers.IO) {
            VioletDatabase.db.spiritDao().run {
                val jobs = mutableListOf<Deferred<List<Long>>>()
                jobs.add(async { upsertAllProperty(properties = propertyDesList) })
                jobs.add(async { upsertAllEggGroup(groups = groupTypeDesList) })
                jobs.add(async { upsertAllSkins(skins = spiritSkinDesList) })
                jobs.add(async { upsertAllSpirits(spirits = spiritDesList) })
                jobs.awaitAll()
            }
        }
    }

    private suspend fun processSkillConfig(nodeRegex: Regex, spiritConfigContent: String) {
        val skillList = mutableListOf<Skill>()
        val talentList = mutableListOf<Talent>()
        withContext(Dispatchers.Default) {
            nodeRegex.findAll(spiritConfigContent).forEachIndexed { index, matchResult ->
                val nodeName = matchResult.groups[1]?.value ?: ""
                val attributes = matchResult.groups[2]?.value ?: ""

                val keyValueRegex = """(\w+)="([^"]*)"""".toRegex()
                val attributeMap = mutableMapOf<String, String>()
                keyValueRegex.findAll(attributes).forEach { result ->
                    val key = result.groups[1]?.value ?: ""
                    val value = result.groups[2]?.value ?: ""
                    attributeMap[key] = value
                }

                when (nodeName) {
                    "SpiritSkillDes" -> {
                        val id = attributeMap["id"] ?: ""
                        if (id.toInt() < 10000) {
                            skillList.add(
                                Skill(
                                    id = id,
                                    name = attributeMap["name"] ?: "",
                                    description = attributeMap["description"] ?: "",
                                    power = attributeMap["power"] ?: "",
                                    ppMax = attributeMap["ppMax"] ?: "",
                                    speed = attributeMap["speed"] ?: "",
                                    property = attributeMap["property"] ?: "",
                                    attackType = attributeMap["attackType"] ?: "",
                                    damageType = attributeMap["damageType"] ?: "",
                                    src = attributeMap["src"] ?: "",
                                )
                            )
                        }
                    }

                    "talentDes" -> {
                        talentList.add(
                            Talent(
                                id = attributeMap["id"] ?: "",
                                name = attributeMap["name"] ?: "",
                                desc = attributeMap["desc"] ?: "",
                            )
                        )
                    }
                }
            }
        }
        withContext(Dispatchers.IO) {
            VioletDatabase.db.skillDao().run {
                val jobs = mutableListOf<Deferred<List<Long>>>()
                jobs.add(async { upsertSkillAll(skillList) })
                jobs.add(async { upsertAllTalents(talentList) })
                jobs.awaitAll()
            }
        }
    }

    private suspend fun processPropConfig(itemRegex: Regex, propConfigContent: String) {
        val props = mutableListOf<Prop>()
        withContext(Dispatchers.Default) {
            itemRegex.findAll(propConfigContent).forEach { matchResult ->
                val itemContent = matchResult.groupValues[1]

                val idRegex = """<ID>(\d+)</ID>""".toRegex()
                val nameRegex = """<Name>(.*?)</Name>""".toRegex()
                val priceRegex = """<Price>(\d+)</Price>""".toRegex()
                val descRegex = """<Desc><!\[CDATA\[(.*?)]]></Desc>""".toRegex()

                val id = idRegex.find(itemContent)?.groupValues?.get(1) ?: "-1"
                val name = nameRegex.find(itemContent)?.groupValues?.get(1) ?: ""
                val price = priceRegex.find(itemContent)?.groupValues?.get(1) ?: ""
                val desc = descRegex.find(itemContent)?.groupValues?.get(1) ?: ""

                props.add(Prop(id = id, name = name, desc = desc, price = price))
            }
        }
        withContext(Dispatchers.IO) {
            VioletDatabase.db.propDao().run {
                upsertAllProps(props)
            }
        }
    }

    private suspend fun calcTime(
        printTime: (Long) -> Unit,
        block: suspend () -> Unit,
    ) {
        val start = System.currentTimeMillis()
        block.invoke()
        val end = System.currentTimeMillis()
        printTime.invoke(end - start)
    }
}