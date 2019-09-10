package com.tendebit.dungeonmaster.charactercreation3.proficiency.data.storage

import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencySelection
import io.reactivex.Maybe

interface DndProficiencyStorage {

	companion object {
		private const val DEFAULT_SELECTION_ID = "default_"

		fun getDefaultId(forItemWithId: CharSequence) = DEFAULT_SELECTION_ID + forItemWithId

	}

	fun storeSelection(selection: DndProficiencySelection, id: CharSequence? = null): CharSequence

	fun findSelectionById(id: CharSequence): Maybe<DndProficiencySelection>

}
