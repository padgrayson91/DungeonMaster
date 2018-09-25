package com.tendebit.dungeonmaster.charactercreation.pages.classselection.model

import android.util.Log
import androidx.annotation.CheckResult
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tendebit.dungeonmaster.charactercreation.TAG
import com.tendebit.dungeonmaster.core.model.StoredResponse
import com.tendebit.dungeonmaster.core.model.StoredResponseDao
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Abstraction + Implementation of a means of retrieving character class data from the dnd5e API
 */
interface CharacterClassInfoSupplier {
    suspend fun getCharacterClasses() : CharacterClassManifest
    suspend fun getClassInfo(directory: CharacterClassDirectory) : CharacterClassInfo

    class Impl(private val dao: StoredResponseDao) : CharacterClassInfoSupplier {
        private companion object {
            const val BASE_URL = "http://dnd5eapi.co/api/"
            const val CLASSES_PATH = "classes/"
        }

        private val client = OkHttpClient()
        private val gson = Gson()

        override suspend fun getCharacterClasses(): CharacterClassManifest {
            attemptExtractStoredResponse(BASE_URL + CLASSES_PATH, CharacterClassManifest::class.java)?.let {
                return it
            }

            Log.d(TAG, "Loading from network")
            val request = Request.Builder()
                    .url(BASE_URL + CLASSES_PATH)
                    .build()

            val response = client.newCall(request).execute()
            val responseBody = storeResponse(BASE_URL + CLASSES_PATH, response)
            return gson.fromJson(responseBody!!, CharacterClassManifest::class.java)
        }

        override suspend fun getClassInfo(directory: CharacterClassDirectory): CharacterClassInfo {
            attemptExtractStoredResponse(directory.url, CharacterClassInfo::class.java)?.let {
                return it
            }

            Log.d(TAG, "Loading from network")
            val request = Request.Builder()
                    .url(directory.url)
                    .build()

            val response = client.newCall(request).execute()
            val responseBody = storeResponse(directory.url, response)
            return gson.fromJson(responseBody!!, CharacterClassInfo::class.java)
        }

        // TODO: by faking the cache headers on responses, okhttp caching can be leveraged instead of the below methods

        private fun <T> attemptExtractStoredResponse(url: String, classOf: Class<T>) : T? {
            val storedResponse = dao.getStoredResponse(url)
            storedResponse?.body?.let {
                return try {
                    gson.fromJson(it, classOf)
                } catch (ignored: JsonSyntaxException) {
                    null
                }
            }
            return null
        }

        @CheckResult
        private fun storeResponse(url: String, response: Response) : String? {
            response.body()?.string()?.let {
                dao.storeResponse(StoredResponse(url, it))
                return it
            }
            return null
        }
    }
}