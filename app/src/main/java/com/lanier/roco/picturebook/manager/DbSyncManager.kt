package com.lanier.roco.picturebook.manager

import android.os.Build
import com.lanier.roco.picturebook.database.VioletDatabase
import com.lanier.roco.picturebook.database.entity.Property
import com.lanier.roco.picturebook.database.entity.Skill
import com.lanier.roco.picturebook.database.entity.Skin
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.database.entity.SpiritGroup
import com.lanier.roco.picturebook.database.entity.Talent
import com.lanier.roco.picturebook.ext.launchSafe
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
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
    private const val UA = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.6261.95 Safari/537.36"

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
                    onStart = onStart,
                    onCompleted = { s, t -> withContext(Dispatchers.Main) { onCompleted.invoke(s, t) } },
                )
            } else {
                syncFromCache(
                    onStart = onStart,
                    onCompleted = { s, t -> withContext(Dispatchers.Main) { onCompleted.invoke(s, t) } },
                )
            }
        }
    }

    private suspend fun syncFromServer(
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
                    decompressAndSaveToLocalCache(inputStream, onCompleted = onCompleted)
                } else {
                    val responseMessage = connection.responseMessage
                    onCompleted.invoke(false, Throwable("网络错误 : $responseCode $responseMessage"))
                }
            } catch (e: Exception) {
                onCompleted.invoke(false, Throwable("错误: ${e.message}"))
            } finally {
                connection.disconnect()
            }
        }
    }

    private suspend fun syncFromCache(
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
            parseContent(content)
        }
        onCompleted.invoke(true, null)
    }

    private suspend fun decompressAndSaveToLocalCache(
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
                        parseStream(outputStream)
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

    private fun parseStream(outputStream: ByteArrayOutputStream) {
        val byteArray = outputStream.toByteArray()
        val content = byteArray.toString(Charsets.UTF_8)
        parseContent(content)
    }

    private fun parseContent(content: String) {
        val propertyDesList = mutableListOf<Property>()
        val groupTypeDesList = mutableListOf<SpiritGroup>()
        val spiritSkinDesList = mutableListOf<Skin>()
        val spiritDesList = mutableListOf<Spirit>()

        val skillList = mutableListOf<Skill>()
        val talentList = mutableListOf<Talent>()

        val spiritConfigRegex = """<SpiritConfig.*?>(.*?)</SpiritConfig>""".toRegex(RegexOption.DOT_MATCHES_ALL)
        val spiritConfigContent = spiritConfigRegex.find(content)?.groups?.get(1)?.value ?: ""

        val skillConfigRegex = """<SpiritSkillConfig.*?>(.*?)</SpiritSkillConfig>""".toRegex(RegexOption.DOT_MATCHES_ALL)
        val skillConfigContent = skillConfigRegex.find(content)?.groups?.get(1)?.value ?: ""

        val nodeRegex = """<(\w+)\s+(.*?)/>""".toRegex()
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

        nodeRegex.findAll(skillConfigContent).forEachIndexed { index, matchResult ->
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

        VioletDatabase.db.spiritDao().run {
            val upsertPropertySize = upsertAllProperty(properties = propertyDesList)
            val upsertEggGroupSize = upsertAllEggGroup(groups = groupTypeDesList)
            val upsertSkinSize = upsertAllSkins(skins = spiritSkinDesList)
            val upsertSpiritSize = upsertAllSpirits(spirits = spiritDesList)
            upsertAllTalents(talentList)
        }

        VioletDatabase.db.skillDao().run {
            upsertSkillAll(skillList)
        }
    }

    private fun calcTime(
        printTime: (Long) -> Unit,
        block: () -> Unit,
    ) {
        val start = System.currentTimeMillis()
        block.invoke()
        val end = System.currentTimeMillis()
        printTime.invoke(end - start)
    }
}