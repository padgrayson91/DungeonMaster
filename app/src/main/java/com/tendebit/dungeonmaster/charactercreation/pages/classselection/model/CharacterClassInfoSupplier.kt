package com.tendebit.dungeonmaster.charactercreation.pages.classselection.model

import android.util.Log
import com.google.gson.Gson
import com.tendebit.dungeonmaster.charactercreation.TAG
import com.tendebit.dungeonmaster.core.model.NetworkResponseStore
import com.tendebit.dungeonmaster.core.model.StoredResponseDao
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Abstraction + Implementation of a means of retrieving character class data from the dnd5e API
 */
interface CharacterClassInfoSupplier {
    suspend fun getCharacterClasses() : CharacterClassManifest
    suspend fun getClassInfo(directory: CharacterClassDirectory) : CharacterClassInfo

    class Impl(dao: StoredResponseDao) : CharacterClassInfoSupplier {
        private companion object {
            const val BASE_URL = "http://dnd5eapi.co/api/"
            const val CLASSES_PATH = "classes/"
        }

        private val client = OkHttpClient()
        private val gson = Gson()
        private val responseStore = NetworkResponseStore(dao)

        override suspend fun getCharacterClasses(): CharacterClassManifest {
            responseStore.attemptExtractStoredResponse(BASE_URL + CLASSES_PATH, CharacterClassManifest::class.java)?.let {
                return it
            }

            Log.d(TAG, "Loading from network")
            val request = Request.Builder()
                    .url(BASE_URL + CLASSES_PATH)
                    .build()

            val response = client.newCall(request).execute()
            val responseBody = responseStore.storeResponse(BASE_URL + CLASSES_PATH, response)
            return gson.fromJson(responseBody!!, CharacterClassManifest::class.java)
        }

        override suspend fun getClassInfo(directory: CharacterClassDirectory): CharacterClassInfo {
            responseStore.attemptExtractStoredResponse(directory.url, CharacterClassInfo::class.java)?.let {
                return it
            }

            Log.d(TAG, "Loading from network")
            val request = Request.Builder()
                    .url(directory.url)
                    .build()

            val response = client.newCall(request).execute()
            val responseBody = responseStore.storeResponse(directory.url, response)
            return gson.fromJson(responseBody!!, CharacterClassInfo::class.java)
        }
    }
}