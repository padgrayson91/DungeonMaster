package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.core.model.Disabled
import com.tendebit.dungeonmaster.core.model.ListItemState
import com.tendebit.dungeonmaster.core.model.Locked
import com.tendebit.dungeonmaster.core.model.Normal
import com.tendebit.dungeonmaster.core.model.Selected
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestDndProficiencyGroup {

	@Test
	fun testInitialSelectionsAreEmpty() {
		val toTest = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 1)
		assert(toTest.selections.isEmpty())
	}

	@Test
	fun testInitialRemainingChoicesMatchesChoiceCount() {
		val toTest = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 1)
		assert(toTest.remainingChoices == 1)
	}

	@Test
	fun testRemainingChoicesDropsAfterItemSelected() {
		val toTest = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		toTest.select(0)
		assert(toTest.remainingChoices == 1)
	}

	@Test
	fun testSelectionEmits() {
		val toTest = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val testObserver = TestObserver<ListItemState<DndProficiency>>()
		toTest.selectionChanges.subscribe(testObserver)

		toTest.select(0)
		testObserver.assertValue { it.index == 0 && it.state is Selected }
	}

	@Test
	fun testAfterSelectionCompletesDisablesEmit() {
		val toTest = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val testObserver = TestObserver<ListItemState<DndProficiency>>()
		toTest.selectionChanges.subscribe(testObserver)

		toTest.select(0)
		toTest.select(1)

		testObserver.assertValueCount(CharacterCreationRobots.blankProficiencyStateList.size)
		for (itemState in testObserver.values()) {
			if (itemState.index == 0 || itemState.index == 1) {
				assert(itemState.state is Selected)
			} else {
				assert(itemState.state is Disabled)
			}
		}
	}

	@Test
	fun testAfterSelectionRevokedNormalStatesEmitAgain() {
		val toTest = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val testObserver = TestObserver<ListItemState<DndProficiency>>()
		toTest.selectionChanges.subscribe(testObserver)

		toTest.select(0)
		toTest.select(1)
		val itemsBeforeRevocation = testObserver.valueCount()
		toTest.deselect(0)

		val itemsAfterRevocation = testObserver.valueCount()
		assert(itemsAfterRevocation - itemsBeforeRevocation == CharacterCreationRobots.blankProficiencyStateList.size - 1) { "Got $itemsAfterRevocation items"}
		for (itemState in testObserver.values().subList(itemsBeforeRevocation + 1, itemsAfterRevocation)) {
			assert(itemState.state is Normal)
		}
	}

	@Test
	fun testGroupWithInitialStateHasCorrectRemainingChoiceCount() {
		val initialGroup = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		initialGroup.select(0)
		initialGroup.select(1)

		val toTest = DndProficiencyGroup(initialGroup.options, 2)
		assert(toTest.remainingChoices == 0)
	}

	@Test
	fun testExternalSelectionLocksMatchingProficiency() {
		val toTest = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val testObserver = TestObserver<ListItemState<DndProficiency>>()
		toTest.selectionChanges.subscribe(testObserver)

		toTest.onExternalSelection(CharacterCreationRobots.standardProficiencyList[1])

		testObserver.assertValue { it.state is Locked && it.state.item == CharacterCreationRobots.standardProficiencyList[1] }
		assert(toTest.remainingChoices == 2)
	}

	@Test
	fun testExternalDeselectionUnlocksMatchingProficiency() {
		val toTest = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val testObserver = TestObserver<ListItemState<DndProficiency>>()
		toTest.selectionChanges.subscribe(testObserver)

		toTest.onExternalSelection(CharacterCreationRobots.standardProficiencyList[1])
		toTest.onExternalDeselection(CharacterCreationRobots.standardProficiencyList[1])

		testObserver.assertValueAt(testObserver.valueCount() - 1) { it.state is Normal && it.state.item == CharacterCreationRobots.standardProficiencyList[1] }
		assert(toTest.remainingChoices == 2)
	}

	@Test
	fun testExternalDeselectionDisablesMatchingPropertyIfSelectionIsComplete() {
		val toTest = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val testObserver = TestObserver<ListItemState<DndProficiency>>()
		toTest.selectionChanges.subscribe(testObserver)
		toTest.onExternalSelection(CharacterCreationRobots.standardProficiencyList[2])
		toTest.select(0)
		toTest.select(1)
		toTest.onExternalDeselection(CharacterCreationRobots.standardProficiencyList[2])

		testObserver.assertValueAt(testObserver.valueCount() - 1) { it.state is Disabled && it.state.item == CharacterCreationRobots.standardProficiencyList[2] }

	}

	@Test
	fun testExternalSelectionNotPresentInGroupDoesNothing() {
		val toTest = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val testObserver = TestObserver<ListItemState<DndProficiency>>()
		toTest.selectionChanges.subscribe(testObserver)

		toTest.onExternalSelection(CharacterCreationRobots.alternateProficiencyList[0])

		testObserver.assertEmpty()
	}

	@Test
	fun testExternalSelectionDoesNotEmitToOutbound() {
		val toTest = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val testObserver = TestObserver<ListItemState<DndProficiency>>()
		toTest.outboundSelectionChanges.subscribe(testObserver)
		toTest.onExternalSelection(CharacterCreationRobots.standardProficiencyList[2])

		assert(testObserver.valueCount() == 0)
	}

	@Test
	fun testCompletingSelectionOnlyEmitsSelectedItemsToOutbound() {
		val toTest = DndProficiencyGroup(CharacterCreationRobots.blankProficiencyStateList, 2)
		val testObserver = TestObserver<ListItemState<DndProficiency>>()
		toTest.outboundSelectionChanges.subscribe(testObserver)

		toTest.select(0)
		toTest.select(1)

		assert(testObserver.valueCount() == 2)
	}

}