package com.tendebit.dungeonmaster.core.model

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Flowable

@Dao
interface StoredCharacterDao {

    @Insert
    fun storeCharacter(character: StoredCharacter)

    @Query("SELECT * FROM player_characters")
    fun getCharacters() : Flowable<List<StoredCharacter>>

    @Delete
    fun deleteCharacter(character: StoredCharacter)
}