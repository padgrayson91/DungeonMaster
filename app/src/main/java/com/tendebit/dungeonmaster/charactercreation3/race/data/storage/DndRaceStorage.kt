package com.tendebit.dungeonmaster.charactercreation3.race.data.storage

import com.tendebit.dungeonmaster.charactercreation3.race.DndRaceSelection
import io.reactivex.Maybe

interface DndRaceStorage {

	companion object {
		const val DEFAULT_SELECTION_ID = "default"
	}

	fun storeSelection(selection: DndRaceSelection, id: CharSequence? = null): CharSequence

	fun findSelectionById(id: CharSequence): Maybe<DndRaceSelection>

}
