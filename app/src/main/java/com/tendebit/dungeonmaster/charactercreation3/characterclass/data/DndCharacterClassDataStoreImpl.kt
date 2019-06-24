package com.tendebit.dungeonmaster.charactercreation3.characterclass.data

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClassSelection
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.network.DndCharacterClassApiConnection
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.storage.DndCharacterClassStorage
import com.tendebit.dungeonmaster.charactercreation3.characterclass.logger
import com.tendebit.dungeonmastercore.model.state.Normal
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DndCharacterClassDataStoreImpl(private val apiConnection: DndCharacterClassApiConnection, private val storage: DndCharacterClassStorage? = null) : DndCharacterClassDataStore {

	private val cachedList = ArrayList<DndCharacterClass>()
	private val networkMutex = Mutex()

	override suspend fun getCharacterClassList(forceNetwork: Boolean): List<DndCharacterClass> {
		networkMutex.withLock  {
			if (cachedList.isNotEmpty() && !forceNetwork) {
				logger.writeDebug("Returning class list from cache")
				return cachedList
			}

			logger.writeDebug("Cache has size ${cachedList.size}")

			if (!forceNetwork) {
				val storedSelection = storage?.findSelectionById(DndCharacterClassStorage.DEFAULT_SELECTION_ID)?.blockingGet()
				if (storedSelection != null) {
					cachedList.clear()
					cachedList.addAll(storedSelection.options.map { it.item!! })
					logger.writeDebug("Returning selection list from db, cache now has size ${cachedList.size}")
					return cachedList
				}
			}

			cachedList.clear()
			cachedList.addAll(apiConnection.getCharacterClasses())
			logger.writeDebug("Fetching class list from network, cache now has size ${cachedList.size}")
			storage?.storeSelection(DndCharacterClassSelection(cachedList.map { Normal(it) }), DndCharacterClassStorage.DEFAULT_SELECTION_ID)
		}
		return cachedList
	}

	override fun restoreCharacterClassList(classList: List<DndCharacterClass>) {
		logger.writeDebug("Caching class list with ${classList.size} elements")
		cachedList.clear()
		cachedList.addAll(classList)
	}

}
