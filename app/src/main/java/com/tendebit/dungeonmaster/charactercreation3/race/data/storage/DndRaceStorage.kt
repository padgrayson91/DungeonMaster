package com.tendebit.dungeonmaster.charactercreation3.race.data.storage

import com.tendebit.dungeonmaster.charactercreation3.race.DndDetailedRace
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmaster.charactercreation3.race.DndRaceSelection
import io.reactivex.Maybe

interface DndRaceStorage {

	companion object {
		const val DEFAULT_SELECTION_ID = "default"
	}

	fun storeSelection(selection: DndRaceSelection, id: CharSequence? = null): CharSequence

	fun storeDetails(detailedRace: DndDetailedRace)

	fun findDetails(origin: DndRace): Maybe<DndDetailedRace>

	fun findSelectionById(id: CharSequence): Maybe<DndRaceSelection>

}
