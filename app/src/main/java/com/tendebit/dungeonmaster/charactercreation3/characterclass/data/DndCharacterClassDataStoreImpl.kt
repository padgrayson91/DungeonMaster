package com.tendebit.dungeonmaster.charactercreation3.characterclass.data

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.network.DndCharacterClassApiConnection
import com.tendebit.dungeonmaster.core.SingletonHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DndCharacterClassDataStoreImpl(private val apiConnection: DndCharacterClassApiConnection) : DndCharacterClassDataStore {

	private val cachedList = ArrayList<DndCharacterClass>()
	private val networkMutex = Mutex()

	override suspend fun getCharacterClassList(forceNetwork: Boolean): List<DndCharacterClass> {
		networkMutex.withLock  {
			if (cachedList.isNotEmpty() && !forceNetwork) {
				return cachedList
			}

			cachedList.clear()
			cachedList.addAll(apiConnection.getCharacterClasses())
		}
		return cachedList
	}

	override fun restoreCharacterClassList(classList: List<DndCharacterClass>) {
		cachedList.clear()
		cachedList.addAll(classList)
	}

}
