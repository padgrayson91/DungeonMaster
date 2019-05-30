package com.tendebit.dungeonmaster.charactercreation3.characterclass

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.Loading
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Selected
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassSelectionViewModel
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.setMain
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

class TestDndCharacterClassSelectionViewModel {

	@Before
	@ExperimentalCoroutinesApi
	fun configureCoroutines() {
		Dispatchers.setMain(Dispatchers.Unconfined)
	}

	@Test
	fun testLoadingWhenStateIsLoading() {
		val mockClassProvider = Mockito.mock(DndCharacterClassProvider::class.java)
		val testExternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		val testInternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.state).thenReturn(Loading)

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider)

		assert(toTest.showLoading)
	}

	@Test
	fun testInitialStateHas1Page() {
		val mockClassProvider = Mockito.mock(DndCharacterClassProvider::class.java)
		val testExternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		val testInternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.state).thenReturn(Loading)

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider)

		assert(toTest.pageCount == 1)
	}

	@Test
	fun testClickingChildEmitsItemChange() = runBlocking {
		val mockClassProvider = Mockito.mock(DndCharacterClassProvider::class.java)
		val testSelection = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testExternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		val testInternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.state).thenReturn(Normal(testSelection))

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider)
		val testObserver = TestObserver<Int>()
		toTest.itemChanges.subscribe(testObserver)

		toTest.children[1].onClick()

		assert(testObserver.valueCount() == 1)
		assert(testObserver.values()[0] == 1)
	}

	@Test
	fun testItemCountIsCorrect() {
		val mockClassProvider = Mockito.mock(DndCharacterClassProvider::class.java)
		val testSelection = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testExternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		val testInternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.state).thenReturn(Normal(testSelection))

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider)

		assert(toTest.itemCount == CharacterCreationRobots.blankClassStateList.size)
	}

	@Test
	fun testNotLoadingWhenNormalStateIsProvided() {
		val mockClassProvider = Mockito.mock(DndCharacterClassProvider::class.java)
		val testSelection = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testExternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		val testInternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.state).thenReturn(Normal(testSelection))

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider)

		assert(!toTest.showLoading)
	}

	@Test
	fun testClickingChildUpdatesChildState() {
		val mockClassProvider = Mockito.mock(DndCharacterClassProvider::class.java)
		val testSelection = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testExternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		val testInternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.state).thenReturn(Normal(testSelection))

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider)

		toTest.children[0].onClick()

		assert(toTest.children[0].state is Selected)
	}

	@Test
	fun testClickingChildTwiceResetsChildState() {
		val mockClassProvider = Mockito.mock(DndCharacterClassProvider::class.java)
		val testSelection = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testExternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		val testInternal = Observable.empty<ItemState<out DndCharacterClassSelection>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.state).thenReturn(Normal(testSelection))

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider)

		toTest.children[0].onClick()
		toTest.children[0].onClick()

		assert(toTest.children[0].state is Normal)
	}

}
