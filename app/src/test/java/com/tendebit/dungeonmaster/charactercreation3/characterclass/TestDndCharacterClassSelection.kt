package com.tendebit.dungeonmaster.charactercreation3.characterclass

import com.tendebit.dungeonmaster.core.model.state.ListItemState
import com.tendebit.dungeonmaster.core.model.state.Normal
import com.tendebit.dungeonmaster.core.model.state.Selected
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestDndCharacterClassSelection {

	@Test
	fun testInitialSelectionIsNull() {
		val toTest = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		assert(toTest.selectedItem == null)
	}

	@Test
	fun testSelectFromBlankState() {
		val toTest = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		toTest.select(0)
		assert(toTest.selectedItem?.item == CharacterCreationRobots.blankClassStateList[0].item)
	}

	@Test
	fun testDeselect() {
		val toTest = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		toTest.select(0)
		toTest.deselect(0)
		assert(toTest.selectedItem == null)
	}

	@Test
	fun testMultipleSelect() {
		val toTest = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		toTest.select(0)
		toTest.select(2)
		assert(toTest.selectedItem?.item == CharacterCreationRobots.blankClassStateList[2].item)
	}

	@Test
	fun testSelectionEmitsToOutbound() {
		val toTest = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testObserver = TestObserver<ListItemState<DndCharacterClass>>()
		toTest.outboundSelectionChanges.subscribe(testObserver)

		toTest.select(0)

		assert(testObserver.valueCount() == 1)
		testObserver.assertValue { it.state is Selected && it.state.item == CharacterCreationRobots.blankClassStateList[0].item }
	}

	@Test
	fun testAutomaticDeselectionDoesNotEmitToOutbound() {
		val toTest = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testObserver = TestObserver<ListItemState<DndCharacterClass>>()
		toTest.outboundSelectionChanges.subscribe(testObserver)

		toTest.select(0)
		toTest.select(1)

		assert(testObserver.valueCount() == 2)
		assert(testObserver.values().all { it.state is Selected })
	}

	@Test
	fun testAutomaticDeselectionEmits() {
		val toTest = DndCharacterClassSelection(CharacterCreationRobots.blankClassStateList)
		val testObserver = TestObserver<ListItemState<DndCharacterClass>>()
		toTest.selectionChanges.subscribe(testObserver)

		toTest.select(0)
		toTest.select(1)

		assert(testObserver.valueCount() == 3)
		assert(testObserver.values().find { it.state is Normal && it.index == 0 } != null)
	}

}