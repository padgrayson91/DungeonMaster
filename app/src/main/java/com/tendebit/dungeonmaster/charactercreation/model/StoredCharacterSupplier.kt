package com.tendebit.dungeonmaster.charactercreation.model

import com.tendebit.dungeonmaster.core.model.StoredCharacter
import com.tendebit.dungeonmaster.core.model.StoredCharacterDao
import io.reactivex.Flowable

interface StoredCharacterSupplier {
    fun getStoredCharacters() : Flowable<List<StoredCharacter>>
    suspend fun saveCharacter(storedCharacter: StoredCharacter)
    suspend fun deleteCharacter(storedCharacter: StoredCharacter)

    class Impl(private val dao: StoredCharacterDao) : StoredCharacterSupplier {
        override fun getStoredCharacters():  Flowable<List<StoredCharacter>> {
            return dao.getCharacters()
        }

        override suspend fun saveCharacter(storedCharacter: StoredCharacter) {
            dao.storeCharacter(storedCharacter)
        }

        override suspend fun deleteCharacter(storedCharacter: StoredCharacter) {
            dao.deleteCharacter(storedCharacter)
        }
    }
}