package com.tendebit.dungeonmaster.charactercreation3

import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencyViewModel
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestDndProficiencyViewModel {

	@Test
	fun testNormalItemYieldsEnabledUnchecked() {
		val testItem = Normal(CharacterCreationRobots.standardProficiencyList[0])
		val toTest = DndProficiencyViewModel(testItem)

		assert(toTest.enabled)
		assert(!toTest.checked)
	}

	@Test
	fun testSelectedItemYieldsEnabledChecked() {
		val testItem = Selected(CharacterCreationRobots.standardProficiencyList[0])
		val toTest = DndProficiencyViewModel(testItem)

		assert(toTest.enabled)
		assert(toTest.checked)
	}

	@Test
	fun testLockedItemYieldsDisabledChecked() {
		val testItem = Locked(CharacterCreationRobots.standardProficiencyList[0])
		val toTest = DndProficiencyViewModel(testItem)

		assert(!toTest.enabled)
		assert(toTest.checked)
	}

	@Test
	fun testSelectionEmits() {
		val testItem = Locked(CharacterCreationRobots.standardProficiencyList[0])
		val toTest = DndProficiencyViewModel(testItem)
		val testObserver = TestObserver<Boolean>()
		toTest.selection.subscribe(testObserver)

		toTest.changeSelection(true)

		assert(testObserver.valueCount() == 1)
		testObserver.assertValue { true }
	}

	@Test
	fun testDuplicateSelectionsDoNotEmit() {
		val testItem = Locked(CharacterCreationRobots.standardProficiencyList[0])
		val toTest = DndProficiencyViewModel(testItem)
		val testObserver = TestObserver<Boolean>()
		toTest.selection.subscribe(testObserver)

		toTest.changeSelection(false)
		toTest.changeSelection(false)

		assert(testObserver.valueCount() == 1)
		testObserver.assertValue { !it }
	}

}