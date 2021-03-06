package com.tendebit.dungeonmaster.charactercreation3.characterclass

import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassSelectionViewModel
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import com.tendebit.dungeonmaster.testhelpers.TestConcurrency
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Loading
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Selected
import com.tendebit.dungeonmastercore.model.state.Selection
import com.tendebit.dungeonmastercore.model.state.SelectionProvider
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

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class TestDndCharacterClassSelectionViewModel {

	private val concurrency = TestConcurrency

	@Before
	fun configureCoroutines() {
		Dispatchers.setMain(Dispatchers.Unconfined)
	}

	@Test
	fun testLoadingWhenStateIsLoading() {
		val mockClassProvider = Mockito.mock(SelectionProvider::class.java) as SelectionProvider<DndCharacterClass>
		val testExternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		val testInternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.selectionState).thenReturn(Loading)

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider, concurrency)

		assert(toTest.showLoading)
	}

	@Test
	fun testInitialStateHas1Page() {
		val mockClassProvider = Mockito.mock(SelectionProvider::class.java) as SelectionProvider<DndCharacterClass>
		val testExternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		val testInternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.selectionState).thenReturn(Loading)

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider, concurrency)

		assert(toTest.pageCount == 1)
	}

	@Test
	fun testClickingChildEmitsItemChange() = runBlocking {
		val mockClassProvider = Mockito.mock(SelectionProvider::class.java) as SelectionProvider<DndCharacterClass>
		val testSelection = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testExternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		val testInternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.selectionState).thenReturn(Normal(testSelection))

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider, concurrency)
		val testObserver = TestObserver<Int>()
		toTest.itemChanges.subscribe(testObserver)

		toTest.children[1].onClick()

		assert(testObserver.valueCount() == 1) { "Expected 1 but had ${testObserver.valueCount()}"}
		assert(testObserver.values()[0] == 1)
	}

	@Test
	fun testItemCountIsCorrect() {
		val mockClassProvider = Mockito.mock(SelectionProvider::class.java) as SelectionProvider<DndCharacterClass>
		val testSelection = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testExternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		val testInternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.selectionState).thenReturn(Normal(testSelection))

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider, concurrency)

		assert(toTest.itemCount == CharacterCreationRobots.blankClassStateList.size)
	}

	@Test
	fun testNotLoadingWhenNormalStateIsProvided() {
		val mockClassProvider = Mockito.mock(SelectionProvider::class.java) as SelectionProvider<DndCharacterClass>
		val testSelection = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testExternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		val testInternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.selectionState).thenReturn(Normal(testSelection))

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider, concurrency)

		assert(!toTest.showLoading)
	}

	@Test
	fun testClickingChildUpdatesChildState() {
		val mockClassProvider = Mockito.mock(SelectionProvider::class.java) as SelectionProvider<DndCharacterClass>
		val testSelection = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testExternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		val testInternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.selectionState).thenReturn(Normal(testSelection))

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider, concurrency)

		toTest.children[0].onClick()

		assert(toTest.children[0].state is Selected)
	}

	@Test
	fun testClickingChildTwiceResetsChildState() {
		val mockClassProvider = Mockito.mock(SelectionProvider::class.java) as SelectionProvider<DndCharacterClass>
		val testSelection = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testExternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		val testInternal = Observable.empty<ItemState<out Selection<DndCharacterClass>>>()
		whenever(mockClassProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockClassProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockClassProvider.selectionState).thenReturn(Normal(testSelection))

		val toTest = DndCharacterClassSelectionViewModel(mockClassProvider, concurrency)

		toTest.children[0].onClick()
		toTest.children[0].onClick()

		assert(toTest.children[0].state is Normal)
	}

}
