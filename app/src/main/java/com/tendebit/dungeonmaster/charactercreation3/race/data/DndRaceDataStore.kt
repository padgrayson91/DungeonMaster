package com.tendebit.dungeonmaster.charactercreation3.race.data

import com.tendebit.dungeonmaster.charactercreation3.race.DndDetailedRace
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace

interface DndRaceDataStore {

	suspend fun getRaceList(forceNetwork: Boolean = false): List<DndRace>

	suspend fun getRaceDetails(race: DndRace, forceNetwork: Boolean = false): DndDetailedRace?

	fun restoreRaceList(classList: List<DndRace>)

}
