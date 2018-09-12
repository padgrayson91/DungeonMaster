package com.tendebit.dungeonmaster.core.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StoredCharacterDao {

    @Insert
    fun storeCharacter(character: StoredCharacter)

    @Query("SELECT * FROM player_characters")
    fun getCharacters() : List<StoredCharacter>
}