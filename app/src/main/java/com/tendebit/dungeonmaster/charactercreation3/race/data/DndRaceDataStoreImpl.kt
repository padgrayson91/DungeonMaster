package com.tendebit.dungeonmaster.charactercreation3.race.data

import com.tendebit.dungeonmaster.charactercreation3.race.DndDetailedRace
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmaster.charactercreation3.race.DndRaceSelection
import com.tendebit.dungeonmaster.charactercreation3.race.data.network.DndRaceApiConnection
import com.tendebit.dungeonmaster.charactercreation3.race.data.storage.DndRaceStorage
import com.tendebit.dungeonmaster.charactercreation3.race.logger
import com.tendebit.dungeonmastercore.model.state.Normal
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DndRaceDataStoreImpl(private val apiConnection: DndRaceApiConnection, private val storage: DndRaceStorage? = null) : DndRaceDataStore {

	private val cachedList = ArrayList<DndRace>()
	private val cachedDetails = HashMap<String, DndDetailedRace?>()
	private val raceListMutex = Mutex()
	private val raceDetailsMutex = Mutex()

	override suspend fun getRaceList(forceNetwork: Boolean): List<DndRace> {
		raceListMutex.withLock  {
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

	override suspend fun getRaceDetails(race: DndRace, forceNetwork: Boolean): DndDetailedRace? {
		raceDetailsMutex.withLock  {
			if (cachedDetails.containsKey(race.detailsUrl) && !forceNetwork) {
				logger.writeDebug("Had cache entry for $race details")
				return cachedDetails[race.detailsUrl]
			}

			if (!forceNetwork) {
				val storedDetails = storage?.findDetails(race)?.blockingGet()
				if (storedDetails != null) {
					cachedDetails[race.detailsUrl] = storedDetails
					logger.writeDebug("Missed cache, but had a db entry for $race")
					return storedDetails
				}
			}

			cachedDetails.remove(race.detailsUrl)
			logger.writeDebug("Loading $race details from network")
			val detailsFromNetwork = apiConnection.getRaceDetails(race)
			cachedDetails[race.detailsUrl] = detailsFromNetwork
			detailsFromNetwork?.let { storage?.storeDetails(it) }
		}
		return cachedDetails[race.detailsUrl]
	}

	override fun restoreRaceList(classList: List<DndRace>) {
		cachedList.clear()
		cachedList.addAll(classList)
	}

}
