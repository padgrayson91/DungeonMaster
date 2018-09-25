package com.tendebit.dungeonmaster.core.model

import androidx.annotation.CheckResult
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.Response

class NetworkResponseStore(private val dao: StoredResponseDao) {
    private val gson = Gson()

    fun <T> attemptExtractStoredResponse(url: String, classOf: Class<T>) : T? {
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
    fun storeResponse(url: String, response: Response) : String? {
        response.body()?.string()?.let {
            dao.storeResponse(StoredResponse(url, it))
            return it
        }
        return null
    }
}