package com.tendebit.dungeonmaster.charactercreation3.proficiency.data

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.network.DndProficiencyApiConnection
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DndProficiencyDataStoreImpl(private val apiConnection: DndProficiencyApiConnection) : DndProficiencyDataStore {

	private val cachedLists = HashMap<String, List<DndProficiencyGroup>>()
	private val networkMutex = Mutex()

	override suspend fun getProficiencyList(characterClass: DndCharacterClass, forceNetwork: Boolean): List<DndProficiencyGroup> {
		networkMutex.withLock  {
			val cachedList = cachedLists[characterClass.detailsUrl]
			if (cachedList?.isNotEmpty() == true && !forceNetwork) {
				return cachedList
			}

			val createdList = apiConnection.getProficiencies(characterClass).map { it.toDndProficiencyGroup() }
			cachedLists[characterClass.detailsUrl] = createdList
			return createdList
		}
	}

	override fun restoreProficiencyList(characterClass: DndCharacterClass, proficiencyList: List<DndProficiencyGroup>) {
		cachedLists[characterClass.detailsUrl] = proficiencyList
	}

}
