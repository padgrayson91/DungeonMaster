package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.network

import android.util.Log
import com.google.gson.Gson
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.TAG
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Abstraction + Implementation of a means of retrieving character class data from the dnd5e API
 */
interface DndCharacterClassApiConnection {

    suspend fun getCharacterClasses() : List<DndCharacterClass>

    class Impl : DndCharacterClassApiConnection {
        private companion object {
            const val BASE_URL = "http://dnd5eapi.co/api/"
            const val CLASSES_PATH = "classes/"
        }

        private val client = OkHttpClient()
        private val gson = Gson()

        override suspend fun getCharacterClasses(): List<DndCharacterClass> {
            val request = Request.Builder()
                    .url(BASE_URL + CLASSES_PATH)
                    .build()

            val response = client.newCall(request).execute()?.body()?.string() ?: return emptyList()
            return gson.fromJson(response, DndCharacterClassManifest::class.java).toClassList()
        }
    }

}
