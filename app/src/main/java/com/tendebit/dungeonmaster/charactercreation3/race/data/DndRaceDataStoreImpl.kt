package com.tendebit.dungeonmaster.charactercreation3.race.data

import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmaster.charactercreation3.race.data.network.DndRaceApiConnection
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DndRaceDataStoreImpl(private val apiConnection: DndRaceApiConnection) : DndRaceDataStore {

	private val cachedList = ArrayList<DndRace>()
	private val networkMutex = Mutex()

	override suspend fun getRaceList(forceNetwork: Boolean): List<DndRace> {
		networkMutex.withLock  {
			if (cachedList.isNotEmpty() && !forceNetwork) {
				return cachedList
			}

			cachedList.clear()
			cachedList.addAll(apiConnection.getRaces())
		}
		return cachedList
	}

	override fun restoreRaceList(classList: List<DndRace>) {
		cachedList.clear()
		cachedList.addAll(classList)
	}

}
