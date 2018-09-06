package com.tendebit.dungeonmaster.sandbox.model

import com.google.gson.Gson
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassManifest
import okhttp3.OkHttpClient
import okhttp3.Request


class SandboxServiceImpl : SandboxService {
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
}