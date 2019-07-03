package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilityDiceRollSelectionViewModel
import com.tendebit.dungeonmaster.testhelpers.TestConcurrency
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Selection
import com.tendebit.dungeonmastercore.model.state.SelectionProvider
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

@ExperimentalCoroutinesApi
@Suppress("UNCHECKED_CAST")
class TestDndAbilityDiceRollSelectionViewModel {

	private val concurrency = TestConcurrency

	@Test
	fun testInitialChildCountMatchesProvider() {
		val selection = DndAbilityRollSelection(6)
		val initialState = Normal(selection)
		val provider = Mockito.mock(SelectionProvider::class.java) as SelectionProvider<Int>
		val internalStateChanges = Observable.empty<ItemState<out Selection<Int>>>()
		val externalStateChanges = Observable.empty<ItemState<out Selection<Int>>>()
		whenever(provider.state).thenReturn(initialState)
		whenever(provider.internalStateChanges).thenReturn(internalStateChanges)
		whenever(provider.externalStateChanges).thenReturn(externalStateChanges)

		val toTest = DndAbilityDiceRollSelectionViewModel(provider, concurrency)
		assert(toTest.itemCount == 6)
	}

	@Test
	fun testOneRollEmitsChange() {
		val selection = DndAbilityRollSelection(6)
		val initialState = Normal(selection)
		val provider = Mockito.mock(SelectionProvider::class.java) as SelectionProvider<Int>
		val internalStateChanges = Observable.empty<ItemState<out Selection<Int>>>()
		val externalStateChanges = Observable.empty<ItemState<out Selection<Int>>>()
		whenever(provider.state).thenReturn(initialState)
		whenever(provider.internalStateChanges).thenReturn(internalStateChanges)
		whenever(provider.externalStateChanges).thenReturn(externalStateChanges)

		val toTest = DndAbilityDiceRollSelectionViewModel(provider, concurrency)
		val testObserver = TestObserver<Int>()
		toTest.itemChanges.subscribe(testObserver)
		selection.manualSet(2, 14)

		assert(testObserver.valueCount() == 1)
		testObserver.assertValue { it == 2 }
	}

	@Test
	fun testChildClick() {
		val selection = DndAbilityRollSelection(6)
		val initialState = Normal(selection)
		val provider = Mockito.mock(SelectionProvider::class.java) as SelectionProvider<Int>
		val internalStateChanges = Observable.empty<ItemState<out Selection<Int>>>()
		val externalStateChanges = Observable.empty<ItemState<out Selection<Int>>>()
		whenever(provider.state).thenReturn(initialState)
		whenever(provider.internalStateChanges).thenReturn(internalStateChanges)
		whenever(provider.externalStateChanges).thenReturn(externalStateChanges)

		selection.autoRollAll()
		val toTest = DndAbilityDiceRollSelectionViewModel(provider, concurrency)
		val testObserver = TestObserver<Int>()
		toTest.itemChanges.subscribe(testObserver)

		toTest.children[0].onClick()
		assert(testObserver.valueCount() == 1)
		testObserver.assertValue { it == 0 }
		assert(toTest.children[0].showHighlight)
	}

	@Test
	fun testChildClickTwice() {
		val selection = DndAbilityRollSelection(6)
		val initialState = Normal(selection)
		val provider = Mockito.mock(SelectionProvider::class.java) as SelectionProvider<Int>
		val internalStateChanges = Observable.empty<ItemState<out Selection<Int>>>()
		val externalStateChanges = Observable.empty<ItemState<out Selection<Int>>>()
		whenever(provider.state).thenReturn(initialState)
		whenever(provider.internalStateChanges).thenReturn(internalStateChanges)
		whenever(provider.externalStateChanges).thenReturn(externalStateChanges)

		selection.autoRollAll()
		val toTest = DndAbilityDiceRollSelectionViewModel(provider, concurrency)
		val testObserver = TestObserver<Int>()
		toTest.itemChanges.subscribe(testObserver)

		toTest.children[4].onClick()
		toTest.children[4].onClick()
		assert(testObserver.valueCount() == 2)
		testObserver.assertValueAt(0, 4)
		testObserver.assertValueAt(1, 4)
		assert(!toTest.children[4].showHighlight)
	}

	@Test
	fun testChildClickedAndAssigned() {
		val selection = DndAbilityRollSelection(6)
		val initialState = Normal(selection)
		val provider = Mockito.mock(SelectionProvider::class.java) as SelectionProvider<Int>
		val internalStateChanges = Observable.empty<ItemState<out Selection<Int>>>()
		val externalStateChanges = Observable.empty<ItemState<out Selection<Int>>>()
		whenever(provider.state).thenReturn(initialState)
		whenever(provider.internalStateChanges).thenReturn(internalStateChanges)
		whenever(provider.externalStateChanges).thenReturn(externalStateChanges)

		selection.autoRollAll()
		val toTest = DndAbilityDiceRollSelectionViewModel(provider, concurrency)
		val testObserver = TestObserver<Int>()
		toTest.itemChanges.subscribe(testObserver)

		toTest.children[5].onClick()
		selection.onAssigned()

		assert(testObserver.valueCount() == 2)
		testObserver.assertValueAt(0, 5)
		testObserver.assertValueAt(1, 5)
		assert(toTest.children[5].disabled)
		assert(!toTest.children[5].showHighlight)
	}


}
