package com.tendebit.dungeonmaster.charactercreation3.proficiency.data

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.network.DndProficiencyApiConnection
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.storage.DndProficiencyStorage
import com.tendebit.dungeonmaster.charactercreation3.proficiency.logger
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DndProficiencyDataStoreImpl(private val apiConnection: DndProficiencyApiConnection, private val storage: DndProficiencyStorage? = null) : DndProficiencyDataStore {

	private val cachedLists = HashMap<String, List<DndProficiencyGroup>>()
	private val classLoadMutex = Mutex()
	private val raceLoadMutex = Mutex()

	override suspend fun getProficiencyList(characterClass: DndCharacterClass, forceNetwork: Boolean): List<DndProficiencyGroup> {
		classLoadMutex.withLock  {
			val key = characterClass.detailsUrl
			val cachedList = cachedLists[key]
			if (cachedList?.isNotEmpty() == true && !forceNetwork) {
				logger.writeDebug("Returning cached proficiencies for $key")
				return cachedList
			}

			if (!forceNetwork) {
				val storedSelection = storage?.findSelectionById(DndProficiencyStorage.getDefaultId(key))?.blockingGet()
				if (storedSelection != null) {
					val updatedList = ArrayList<DndProficiencyGroup>()
					updatedList.addAll(storedSelection.groupStates.map { it.item!! })
					cachedLists[key] = updatedList
					logger.writeDebug("Returning selection list from db with ${updatedList.size} groups for ${characterClass.name}")
					return updatedList
				}
			}

			logger.writeDebug("Fetching proficiencies from network for $key")
			val createdList = apiConnection.getProficiencies(characterClass).map { it.toDndProficiencyGroup() }
			cachedLists[characterClass.detailsUrl] = createdList
			storage?.storeSelection(DndProficiencySelection(createdList), DndProficiencyStorage.getDefaultId(key))
			return createdList
		}
	}

	override suspend fun getProficiencyList(race: DndRace, forceNetwork: Boolean): List<DndProficiencyGroup> {
		raceLoadMutex.withLock  {
			val key = race.detailsUrl
			val cachedList = cachedLists[key]
			if (cachedList?.isNotEmpty() == true && !forceNetwork) {
				logger.writeDebug("Returning cached proficiencies for $key")
				return cachedList
			}

			if (!forceNetwork) {
				val storedSelection = storage?.findSelectionById(DndProficiencyStorage.getDefaultId(key))?.blockingGet()
				if (storedSelection != null) {
					val updatedList = ArrayList<DndProficiencyGroup>()
					updatedList.addAll(storedSelection.groupStates.map { it.item!! })
					cachedLists[key] = updatedList
					logger.writeDebug("Returning selection list from db with ${updatedList.size} groups for ${race.name}")
					return updatedList
				}
			}

			logger.writeDebug("Fetching proficiencies from network for $key")
			val createdList = apiConnection.getProficiencies(race).map { it.toDndProficiencyGroup() }
			cachedLists[race.detailsUrl] = createdList
			storage?.storeSelection(DndProficiencySelection(createdList), DndProficiencyStorage.getDefaultId(key))
			return createdList
		}
	}

	override fun restoreProficiencyList(characterClass: DndCharacterClass, proficiencyList: List<DndProficiencyGroup>) {
		cachedLists[characterClass.detailsUrl] = proficiencyList
	}

}
