package com.tendebit.dungeonmaster.charactercreation3.characterclass.data

import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.network.DndCharacterClassApiConnection
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

class TestDndCharacterClassDataStore {

	@Test
	fun testNetworkOnlyCalledOnceForMultipleConcurrentCacheReads() = runBlocking {
		val mockApi = Mockito.mock(DndCharacterClassApiConnection::class.java)
		whenever(mockApi.getCharacterClasses()).then {
			runBlocking {
				delay(1000)
			}
			return@then CharacterCreationRobots.standardClassListV2
		}.thenThrow(RuntimeException("Called API more than once!"))

		val toTest = DndCharacterClassDataStoreImpl(mockApi)
		launch { toTest.getCharacterClassList() }
		launch { toTest.getCharacterClassList() }

		assert(toTest.getCharacterClassList().isNotEmpty())
	}

}
