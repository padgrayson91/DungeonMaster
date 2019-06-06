package com.tendebit.dungeonmaster.charactercreation3.race.data.network

import com.google.gson.Gson
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Abstraction + Implementation of a means of retrieving character class data from the dnd5e API
 */
interface DndRaceApiConnection {

    suspend fun getRaces() : List<DndRace>

    class Impl : DndRaceApiConnection {
        private companion object {
            const val BASE_URL = "http://dnd5eapi.co/api/"
            const val RACES_PATH = "races/"
        }

        private val client = OkHttpClient()
        private val gson = Gson()

        override suspend fun getRaces(): List<DndRace> {
            val request = Request.Builder()
                    .url(BASE_URL + RACES_PATH)
                    .build()

            val response = client.newCall(request).execute()?.body()?.string() ?: return emptyList()
            return gson.fromJson(response, DndRaceManifest::class.java).toClassList()
        }
    }

}
