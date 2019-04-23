package com.tendebit.dungeonmaster.charactercreation3

import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation3.proficiency.ProficiencyProvider
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencySelectionViewModel
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import io.reactivex.Observable
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

class TestDndProficiencySelectionViewModel {

	@Test
	fun testInitialViewModelHasZeroPages() {
		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		whenever(mockProvider.proficiencyOptions).thenReturn(Observable.empty())

		val toTest = DndProficiencySelectionViewModel(mockProvider)
		assert(toTest.pageCount == 0)
	}

	@Test
	fun testInitialViewModelDoesNotShowLoading() {
		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		whenever(mockProvider.proficiencyOptions).thenReturn(Observable.empty())

		val toTest = DndProficiencySelectionViewModel(mockProvider)
		assert(!toTest.showLoading)
	}

	@Test
	fun testShowsLoadingWhenSelectionStateIsUndefined() {
		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		whenever(mockProvider.proficiencyOptions).thenReturn(Observable.fromArray(Undefined))

		val toTest = DndProficiencySelectionViewModel(mockProvider)

		assert(toTest.showLoading)
	}

	@Test
	fun testPageCountMatchesNumberOfGroupsFromSelection() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)

		val testSelection = DndProficiencySelection(listOf(groupA, groupB))

		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		whenever(mockProvider.proficiencyOptions).thenReturn(Observable.fromArray(Normal (testSelection)))

		val toTest = DndProficiencySelectionViewModel(mockProvider)
		assert(toTest.pageCount == 2)
	}

	@Test
	fun testFirstPageOfTwoAllowsForwardAndBackNavigationInNormalState() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)

		val testSelection = DndProficiencySelection(listOf(groupA, groupB))

		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		whenever(mockProvider.proficiencyOptions).thenReturn(Observable.fromArray(Normal (testSelection)))

		val toTest = DndProficiencySelectionViewModel(mockProvider)
		val firstPageActions = toTest.getPageActions(0)
		assert(firstPageActions.contains(PageAction.NAVIGATE_BACK) && firstPageActions.contains(PageAction.NAVIGATE_NEXT))
	}

	@Test
	fun testSecondPageOfTwoOnlyAllowsBackNavigationInNormalState() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)

		val testSelection = DndProficiencySelection(listOf(groupA, groupB))

		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		whenever(mockProvider.proficiencyOptions).thenReturn(Observable.fromArray(Normal (testSelection)))

		val toTest = DndProficiencySelectionViewModel(mockProvider)
		val secondPageActions = toTest.getPageActions(1)
		assert(secondPageActions.contains(PageAction.NAVIGATE_BACK) && !secondPageActions.contains(PageAction.NAVIGATE_NEXT))
	}

	@Test
	fun testSecondPageOfTwoAllowsForwardAndBackNavigationInCompletedState() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)

		val testSelection = DndProficiencySelection(listOf(groupA, groupB))

		val mockProvider = Mockito.mock(ProficiencyProvider::class.java)
		whenever(mockProvider.proficiencyOptions).thenReturn(Observable.fromArray(Completed(testSelection)))

		val toTest = DndProficiencySelectionViewModel(mockProvider)
		val secondPageActions = toTest.getPageActions(1)
		assert(secondPageActions.contains(PageAction.NAVIGATE_BACK) && secondPageActions.contains(PageAction.NAVIGATE_NEXT))
	}

}
