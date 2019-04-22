package com.tendebit.dungeonmaster.charactercreation3

import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencySelection
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestDndProficiencySelection {

	@Test
	fun testGroupsWithoutSelectionsHaveNormalState() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)

		val toTest = DndProficiencySelection(listOf(groupA, groupB))

		assert(toTest.selections.isEmpty())
		assert(toTest.groupStates.all { it is Normal })
	}

	@Test
	fun testGroupWithSelectionsCompleteIsComplete() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 0)

		val toTest = DndProficiencySelection(listOf(groupA, groupB))

		assert(toTest.selections.isEmpty())
		assert(toTest.groupStates[1] is Completed)
	}

	@Test
	fun testSelectionsContainSelectionsFromGroups() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)

		val toTest = DndProficiencySelection(listOf(groupA, groupB))
		groupA.select(0)
		groupB.select(1)

		assert(toTest.selections.contains(groupA.options[0].item) && toTest.selections.contains(groupB.options[1].item))
	}

	@Test
	fun testSelectionFromOneGroupLocksSameItemInAnotherGroup() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)

		DndProficiencySelection(listOf(groupA, groupB))
		groupA.select(0)

		assert(groupB.options[0] is Locked)
	}

	@Test
	fun testDeselectionFromOneGroupUnlocksSameItemInAnotherGroup() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)

		DndProficiencySelection(listOf(groupA, groupB))
		groupA.select(0)
		assert(groupB.options[0] is Locked)


		groupA.deselect(0)
		assert(groupB.options[0] is Normal)
	}

	@Test
	fun testCompleteSelection() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)

		val toTest = DndProficiencySelection(listOf(groupA, groupB))
		val testObserver = TestObserver<ListItemState<DndProficiencyGroup>>()
		toTest.stateChanges.subscribe(testObserver)

		groupA.select(0)
		groupA.select(2)

		testObserver.assertValueCount(1)
		testObserver.assertValueAt(0) { it.state is Completed && it.index == 0 }

		groupB.select(1)
		groupB.select(3)

		testObserver.assertValueCount(2)
		testObserver.assertValueAt(1) { it.state is Completed && it.index == 1 }
	}

	@Test
	fun testCompleteSelectionAndThenRevoke() {
		val groupA = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val groupB = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)

		val toTest = DndProficiencySelection(listOf(groupA, groupB))
		val testObserver = TestObserver<ListItemState<DndProficiencyGroup>>()
		toTest.stateChanges.subscribe(testObserver)

		groupA.select(0)
		groupA.select(2)
		groupB.select(1)
		groupB.select(3)
		groupA.deselect(2)

		testObserver.assertValueCount(3)
		testObserver.assertValueAt(2) { it.state is Normal && it.index == 0 }
	}

}