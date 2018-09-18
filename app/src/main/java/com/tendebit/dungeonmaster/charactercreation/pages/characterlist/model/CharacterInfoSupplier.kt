package com.tendebit.dungeonmaster.charactercreation.pages.characterlist.model

import com.tendebit.dungeonmaster.core.model.StoredCharacter
import com.tendebit.dungeonmaster.core.model.StoredCharacterDao
import io.reactivex.Flowable

interface CharacterInfoSupplier {
    suspend fun delete(character: StoredCharacter)
    fun getStoredCharacters() : Flowable<List<StoredCharacter>>

    class Impl(private val dao: StoredCharacterDao) : CharacterInfoSupplier {
        override suspend fun delete(character: StoredCharacter) {
            return dao.deleteCharacter(character)
        }

        override fun getStoredCharacters(): Flowable<List<StoredCharacter>> {
            return dao.getCharacters()
        }
    }
}