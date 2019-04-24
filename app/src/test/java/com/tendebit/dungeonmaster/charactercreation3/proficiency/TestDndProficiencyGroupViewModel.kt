package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Selected
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencyGroupViewModel
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencyViewModel
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestDndProficiencyGroupViewModel {

	@Test
	fun testInitialViewModelRemainingChoicesMatchGroup() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val toTest = DndProficiencyGroupViewModel(Normal(groupA))

		assert(toTest.remainingChoices == 2)
	}

	@Test
	fun testViewModelChildCountMatchesGroupOptionCount() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val toTest = DndProficiencyGroupViewModel(Normal(groupA))

		assert(toTest.children.size == groupA.options.size)
	}

	@Test
	fun testUpdatingGroupSelectionUpdatesViewModelRemainingChoices() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val toTest = DndProficiencyGroupViewModel(Normal(groupA))

		groupA.select(0)
		assert(toTest.remainingChoices == 1)
	}

	@Test
	fun testUpdatingGroupSelectionCausesViewModelChangeToEmit() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val toTest = DndProficiencyGroupViewModel(Normal(groupA))
		val testObserver = TestObserver<DndProficiencyGroupViewModel>()
		toTest.changes.subscribe(testObserver)
		groupA.select(0)
		assert(testObserver.valueCount() == 1)
	}

	@Test
	fun testDeselectingFromGroupUpdatesViewModelRemainingChoices() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val toTest = DndProficiencyGroupViewModel(Normal(groupA))

		groupA.select(0)
		groupA.deselect(0)
		assert(toTest.remainingChoices == 2)
	}

	@Test
	fun testDeselectingFromGroupCausesViewModelChangeToEmit() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val toTest = DndProficiencyGroupViewModel(Normal(groupA))
		val testObserver = TestObserver<DndProficiencyGroupViewModel>()
		toTest.changes.subscribe(testObserver)
		groupA.select(0)
		groupA.deselect(0)
		assert(testObserver.valueCount() == 2)
	}

	@Test
	fun testSelectionFromChildUpdatesChildAndSelf() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val toTest = DndProficiencyGroupViewModel(Normal(groupA))
		val testObserver = TestObserver<DndProficiencyGroupViewModel>()
		val childTestObserver = TestObserver<DndProficiencyViewModel>()
		toTest.changes.subscribe(testObserver)
		toTest.children[0].changes.subscribe(childTestObserver)

		toTest.children[0].changeSelection(true)

		assert(toTest.remainingChoices == 1)
		assert(testObserver.valueCount() == 1)
		assert(toTest.children[0].state is Selected)
		assert(childTestObserver.valueCount() == 1)
	}

}