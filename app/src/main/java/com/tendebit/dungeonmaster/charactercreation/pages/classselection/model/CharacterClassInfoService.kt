package com.tendebit.dungeonmaster.charactercreation.pages.classselection.model

import android.content.Context
import android.util.Log
import androidx.annotation.CheckResult
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.model.StoredResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

interface CharacterClassInfoService {
    suspend fun getCharacterClasses() : CharacterClassManifest
    suspend fun getClassInfo(directory: CharacterClassDirectory) : CharacterClassInfo

    class Impl(c: Context) : CharacterClassInfoService {
        private companion object {
            const val BASE_URL = "http://dnd5eapi.co/api/"
            const val CLASSES_PATH = "classes/"
        }

        private val client = OkHttpClient()
        private val gson = Gson()
        private val db : DnDDatabase = DnDDatabase.getInstance(c.applicationContext)

        override suspend fun getCharacterClasses(): CharacterClassManifest {
            attemptExtractStoredResponse(BASE_URL + CLASSES_PATH, CharacterClassManifest::class.java)?.let {
                return it
            }

            Log.d("CHARACTER_CREATION", "Loading from network")
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

            Log.d("CHARACTER_CREATION", "Loading from network")
            val request = Request.Builder()
                    .url(directory.url)
                    .build()

            val response = client.newCall(request).execute()
            val responseBody = storeResponse(directory.url, response)
            return gson.fromJson(responseBody!!, CharacterClassInfo::class.java)
        }

        private fun <T> attemptExtractStoredResponse(url: String, classOf: Class<T>) : T? {
            val storedResponse = db.responseDao().getStoredResponse(url)
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
                db.responseDao().storeResponse(StoredResponse(url, it))
                return it
            }
            return null
        }
    }
}