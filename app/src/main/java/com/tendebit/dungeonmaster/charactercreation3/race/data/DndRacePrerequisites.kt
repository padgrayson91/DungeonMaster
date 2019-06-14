package com.tendebit.dungeonmaster.charactercreation3.race.data

import com.tendebit.dungeonmaster.core.concurrency.Concurrency

interface DndRacePrerequisites {

	val concurrency: Concurrency
	val dataStore: DndRaceDataStore

	class Impl(override val concurrency: Concurrency, override val dataStore: DndRaceDataStore) : DndRacePrerequisites

}
