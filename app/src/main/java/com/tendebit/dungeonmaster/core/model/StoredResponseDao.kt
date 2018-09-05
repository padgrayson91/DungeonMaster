package com.tendebit.dungeonmaster.core.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StoredResponseDao {

    @Insert
    fun storeResponse(response: StoredResponse)

    @Query("SELECT * FROM dnd5eResponses WHERE url = :url")
    fun getStoredResponse(url : String) : StoredResponse?
}