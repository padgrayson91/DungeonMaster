package com.tendebit.dungeonmaster.charactercreation3.characterclass.data

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass

interface DndCharacterClassDataStore {

	suspend fun getCharacterClassList(forceNetwork: Boolean = false): List<DndCharacterClass>

	fun restoreCharacterClassList(classList: List<DndCharacterClass>)

}
