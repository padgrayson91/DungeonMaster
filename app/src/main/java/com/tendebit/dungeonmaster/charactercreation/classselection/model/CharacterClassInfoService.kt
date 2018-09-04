package com.tendebit.dungeonmaster.charactercreation.classselection.model

import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request

interface CharacterClassInfoService {
    suspend fun getCharacterClasses() : CharacterClassManifest
    suspend fun getClassInfo(directory: CharacterClassDirectory) : CharacterClassInfo

    class Impl : CharacterClassInfoService {
        private companion object {
            const val BASE_URL = "http://dnd5eapi.co/api/"
            const val CLASSES_PATH = "classes/"
        }

        private val client = OkHttpClient()
        private val gson = Gson()

        override suspend fun getCharacterClasses(): CharacterClassManifest {
            val request = Request.Builder()
                    .url(BASE_URL + CLASSES_PATH)
                    .build()

            val response = client.newCall(request).execute()
            return gson.fromJson(response.body()!!.string(), CharacterClassManifest::class.java)
        }

        override suspend fun getClassInfo(directory: CharacterClassDirectory): CharacterClassInfo {
            val request = Request.Builder()
                    .url(directory.url)
                    .build()

            val response = client.newCall(request).execute()
            return gson.fromJson(response.body()!!.string(), CharacterClassInfo::class.java)
        }
    }
}