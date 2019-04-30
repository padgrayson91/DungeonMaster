package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Removed
import com.tendebit.dungeonmaster.charactercreation3.Undefined
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencySelectionViewModel
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

class TestDndProficiencySelectionViewModel {

	@Test
	fun testHasZeroPagesForRemovedState() {
		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		val testExternal = Observable.empty<ItemState<out DndProficiencySelection>>()
		val testInternal = Observable.empty<ItemState<out DndProficiencySelection>>()
		whenever(mockProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockProvider.state).thenReturn(Removed)

		val toTest = DndProficiencySelectionViewModel(mockProvider)
		assert(toTest.pageCount == 0)
	}

	@Test
	fun testDoesNotShowLoadingForRemovedState() {
		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		val testExternal = Observable.empty<ItemState<out DndProficiencySelection>>()
		val testInternal = Observable.empty<ItemState<out DndProficiencySelection>>()
		whenever(mockProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockProvider.state).thenReturn(testExternal.mergeWith(testInternal).last(Removed).blockingGet())

		val toTest = DndProficiencySelectionViewModel(mockProvider)
		assert(!toTest.showLoading)
	}

	@Test
	fun testShowsLoadingForUndefinedStateButHasZeroPages() {
		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		val testExternal = Observable.fromArray<ItemState<out DndProficiencySelection>>(Undefined)
		val testInternal = Observable.empty<ItemState<out DndProficiencySelection>>()
		whenever(mockProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockProvider.state).thenReturn(Undefined)

		val toTest = DndProficiencySelectionViewModel(mockProvider)

		assert(toTest.showLoading)
		assert(toTest.pageCount == 0)
	}

	@Test
	fun testPageCountMatchesNumberOfGroupsFromSelection() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)

		val testSelection = DndProficiencySelection(listOf(groupA, groupB))

		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		val testExternal = Observable.empty<ItemState<out DndProficiencySelection>>()
		val testInternal = Observable.empty<ItemState<out DndProficiencySelection>>()
		whenever(mockProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockProvider.state).thenReturn(Normal(testSelection))

		val toTest = DndProficiencySelectionViewModel(mockProvider)
		assert(toTest.pageCount == 2)
	}

	@Test
	fun testChildStateIsCompletedAfterSelection() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 1)

		val testSelection = DndProficiencySelection(listOf(groupA, groupB))

		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		val testExternal = Observable.empty<ItemState<out DndProficiencySelection>>()
		val testInternal = Observable.empty<ItemState<out DndProficiencySelection>>()
		whenever(mockProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockProvider.state).thenReturn(Normal(testSelection))


		val toTest = DndProficiencySelectionViewModel(mockProvider)
		groupA.select(0)
		groupA.select(1)

		assert(toTest.pages[0].state is Completed)
	}

	@Test
	fun testPagesAddedWhenSelectionIsUpdatedToIncludeMoreGroups() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 1)
		val groupC = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 1)

		val testSelection = DndProficiencySelection(listOf(groupA))
		val testSelection2 = DndProficiencySelection(listOf(groupA, groupB, groupC))
		var state = Normal(testSelection)

		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		val testExternal = PublishSubject.create<ItemState<out DndProficiencySelection>>()
		val testInternal = Observable.empty<ItemState<out DndProficiencySelection>>()
		whenever(mockProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockProvider.state).thenAnswer { state }

		val toTest = DndProficiencySelectionViewModel(mockProvider)
		val testObserver = TestObserver<Int>()
		toTest.pageAdditions.subscribe(testObserver)
		state = Normal(testSelection2)
		testExternal.onNext(state)

		assert(toTest.pageCount == 3)
		assert(testObserver.valueCount() == 2)
		assert(testObserver.values()[0] == 1)
		assert(testObserver.values()[1] == 2)
	}

	@Test
	fun testPagesRemovedWhenSelectionIsUpdatedToIncludeFewerGroups() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 1)
		val groupC = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 1)

		val testSelection = DndProficiencySelection(listOf(groupA))
		val testSelection2 = DndProficiencySelection(listOf(groupA, groupB, groupC))
		var state = Normal(testSelection2)

		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		val testExternal = PublishSubject.create<ItemState<out DndProficiencySelection>>()
		val testInternal = Observable.empty<ItemState<out DndProficiencySelection>>()
		whenever(mockProvider.externalStateChanges).thenReturn(testExternal)
		whenever(mockProvider.internalStateChanges).thenReturn(testInternal)
		whenever(mockProvider.state).thenAnswer { state }

		val toTest = DndProficiencySelectionViewModel(mockProvider)
		val testObserver = TestObserver<Int>()
		toTest.pageRemovals.subscribe(testObserver)
		state = Normal(testSelection)
		testExternal.onNext(state)

		assert(toTest.pageCount == 1)
		assert(testObserver.valueCount() == 2)
		assert(testObserver.values()[0] == 2)
		assert(testObserver.values()[1] == 1)
	}

}
