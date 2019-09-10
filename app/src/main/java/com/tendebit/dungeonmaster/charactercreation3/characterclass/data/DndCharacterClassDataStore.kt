package com.tendebit.dungeonmaster.charactercreation3.characterclass.data

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndDetailedCharacterClass

/**
 * Broad abstraction for remote, disk, and cache repositories for [DndCharacterClass] data
 */
interface DndCharacterClassDataStore {

	suspend fun getCharacterClassList(forceNetwork: Boolean = false): List<DndCharacterClass>

	suspend fun getCharacterClassDetails(dndClass: DndCharacterClass): DndDetailedCharacterClass?

	fun restoreCharacterClassList(classList: List<DndCharacterClass>)

}
