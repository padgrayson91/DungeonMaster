package com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model

import android.util.Log
import androidx.annotation.CheckResult
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.tendebit.dungeonmaster.core.model.DnDDatabase
import com.tendebit.dungeonmaster.core.model.StoredResponse
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Abstraction + implementation of a means of retrieving race info data from the dnd5e API
 */
interface CharacterRaceInfoSupplier {
    suspend fun getCharacterRaces() : CharacterRaceManifest

    class Impl(private val db: DnDDatabase) : CharacterRaceInfoSupplier {
        private companion object {
            const val BASE_URL = "http://dnd5eapi.co/api/"
            const val RACES_PATH = "races/"
        }

        private val client = OkHttpClient()
        private val gson = Gson()

        override suspend fun getCharacterRaces(): CharacterRaceManifest {
            attemptExtractStoredResponse(BASE_URL + RACES_PATH, CharacterRaceManifest::class.java)?.let {
                return it
            }

            Log.d("CHARACTER_CREATION", "Loading from network")
            val request = Request.Builder()
                    .url(BASE_URL + RACES_PATH)
                    .build()

            val response = client.newCall(request).execute()
            val responseBody = storeResponse(BASE_URL + RACES_PATH, response)
            return gson.fromJson(responseBody!!, CharacterRaceManifest::class.java)
        }

        // TODO: by faking the cache headers on responses, okhttp caching can be leveraged instead of the below methods

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