package com.tendebit.dungeonmaster.charactercreation3.race.data

import com.tendebit.dungeonmaster.charactercreation3.race.DndRace

interface DndRaceDataStore {

	suspend fun getRaceList(forceNetwork: Boolean = false): List<DndRace>

	fun restoreRaceList(classList: List<DndRace>)

}
