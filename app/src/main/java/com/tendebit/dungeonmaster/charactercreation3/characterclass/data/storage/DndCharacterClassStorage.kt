package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClassSelection
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndDetailedCharacterClass
import io.reactivex.Maybe

/**
 * Abstraction for a database containing information related to [DndCharacterClassSelection] objects
 */
interface DndCharacterClassStorage {

	companion object {
		const val DEFAULT_SELECTION_ID = "default"
	}

	fun storeSelection(selection: DndCharacterClassSelection, id: CharSequence? = null): CharSequence

	fun storeDetails(details: DndDetailedCharacterClass)

	fun findDetails(origin: DndCharacterClass): Maybe<DndDetailedCharacterClass>

	fun findSelectionById(id: CharSequence): Maybe<DndCharacterClassSelection>

}
