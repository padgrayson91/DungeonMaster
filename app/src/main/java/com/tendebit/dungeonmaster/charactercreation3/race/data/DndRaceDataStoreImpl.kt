package com.tendebit.dungeonmaster.charactercreation3.race.data

import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmaster.charactercreation3.race.DndRaceSelection
import com.tendebit.dungeonmaster.charactercreation3.race.data.network.DndRaceApiConnection
import com.tendebit.dungeonmaster.charactercreation3.race.data.storage.DndRaceStorage
import com.tendebit.dungeonmaster.charactercreation3.race.logger
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DndRaceDataStoreImpl(private val apiConnection: DndRaceApiConnection, private val storage: DndRaceStorage? = null) : DndRaceDataStore {

	private val cachedList = ArrayList<DndRace>()
	private val networkMutex = Mutex()

	override suspend fun getRaceList(forceNetwork: Boolean): List<DndRace> {
		networkMutex.withLock  {
			if (cachedList.isNotEmpty() && !forceNetwork) {
				return cachedList
			}

			if (!forceNetwork) {
				val storedSelection = storage?.findSelectionById(DndRaceStorage.DEFAULT_SELECTION_ID)?.blockingGet()
				if (storedSelection != null) {
					cachedList.clear()
					cachedList.addAll(storedSelection.options.map { it.item!! })
					logger.writeDebug("Returning selection list from db, cache now has size ${cachedList.size}")
					return cachedList
				}
			}

			cachedList.clear()
			cachedList.addAll(apiConnection.getRaces())
			storage?.storeSelection(DndRaceSelection(cachedList.map { Normal(it) }), DndRaceStorage.DEFAULT_SELECTION_ID)

		}
		return cachedList
	}

	override fun restoreRaceList(classList: List<DndRace>) {
		cachedList.clear()
		cachedList.addAll(classList)
	}

}
