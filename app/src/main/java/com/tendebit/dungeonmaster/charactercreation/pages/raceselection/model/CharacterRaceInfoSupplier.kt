package com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model

import android.util.Log
import com.google.gson.Gson
import com.tendebit.dungeonmaster.charactercreation.TAG
import com.tendebit.dungeonmaster.core.model.NetworkResponseStore
import com.tendebit.dungeonmaster.core.model.StoredResponseDao
import okhttp3.OkHttpClient
import okhttp3.Request

/**
 * Abstraction + implementation of a means of retrieving race info data from the dnd5e API
 */
interface CharacterRaceInfoSupplier {
    suspend fun getCharacterRaces() : CharacterRaceManifest

    class Impl(dao: StoredResponseDao) : CharacterRaceInfoSupplier {
        private companion object {
            const val BASE_URL = "http://dnd5eapi.co/api/"
            const val RACES_PATH = "races/"
        }

        private val client = OkHttpClient()
        private val gson = Gson()
        private val responseStore = NetworkResponseStore(dao)

        override suspend fun getCharacterRaces(): CharacterRaceManifest {
            responseStore.attemptExtractStoredResponse(BASE_URL + RACES_PATH,
                    CharacterRaceManifest::class.java)?.let {
                return it
            }

            Log.d(TAG, "Loading from network")
            val request = Request.Builder()
                    .url(BASE_URL + RACES_PATH)
                    .build()

            val response = client.newCall(request).execute()
            val responseBody = responseStore.storeResponse(BASE_URL + RACES_PATH, response)
            return gson.fromJson(responseBody!!, CharacterRaceManifest::class.java)
        }
    }
}