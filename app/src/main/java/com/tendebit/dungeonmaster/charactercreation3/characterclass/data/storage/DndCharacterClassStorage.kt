package com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClassSelection
import io.reactivex.Maybe

interface DndCharacterClassStorage {

	companion object {
		const val DEFAULT_SELECTION_ID = "default"
	}

	fun storeSelection(selection: DndCharacterClassSelection, id: CharSequence? = null): CharSequence

	fun findSelectionById(id: CharSequence): Maybe<DndCharacterClassSelection>

}
