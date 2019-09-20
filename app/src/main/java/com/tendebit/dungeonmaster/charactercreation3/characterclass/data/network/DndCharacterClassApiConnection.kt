package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.network

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndDetailedCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.logger
import com.tendebit.dungeonmastercore.network.NetworkEnvironment
import okhttp3.Request
import java.io.IOException

/**
 * Abstraction + Implementation of a means of retrieving character class data from the dnd5e API
 */
interface DndCharacterClassApiConnection {

    suspend fun getCharacterClasses() : List<DndCharacterClass>

    suspend fun getClassDetails(sourceClass: DndCharacterClass): DndDetailedCharacterClass?

    class Impl(private val environment: NetworkEnvironment) : DndCharacterClassApiConnection {
        private companion object {
            const val BASE_URL = "http://dnd5eapi.co/api/"
            const val CLASSES_PATH = "classes/"
        }

        override suspend fun getCharacterClasses(): List<DndCharacterClass> {
            return try {
                val request = Request.Builder()
                        .url(BASE_URL + CLASSES_PATH)
                        .build()
                val response = environment.client.newCall(request).execute()?.body()?.string() ?: return emptyList()
                environment.gson.fromJson(response, DndCharacterClassManifest::class.java).toClassList()
            } catch (ex: IOException) {
                logger.writeDebug("Unable to load classes from network")
                emptyList()
            }
        }

        override suspend fun getClassDetails(sourceClass: DndCharacterClass): DndDetailedCharacterClass? {
            return try {
                val request = Request.Builder()
                        .url(sourceClass.detailsUrl)
                        .build()

                val response = environment.client.newCall(request).execute()?.body()?.string() ?: return null
                val classDetail = environment.gson.fromJson(response, DndCharacterClassInfo::class.java)
                classDetail.toDetailedCharacterClass()
            } catch (ex: IOException) {
                logger.writeDebug("Unable to load class details from network")
                null
            }
        }
    }

}
