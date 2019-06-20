package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencyViewModel
import com.tendebit.dungeonmaster.core.model.Disabled
import com.tendebit.dungeonmaster.core.model.Locked
import com.tendebit.dungeonmaster.core.model.Normal
import com.tendebit.dungeonmaster.core.model.Selected
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import com.tendebit.dungeonmaster.testhelpers.TestConcurrency
import io.reactivex.observers.TestObserver
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class TestDndProficiencyViewModel {

	private val concurrency = TestConcurrency

	@Test
	fun testNormalItemYieldsEnabledUnchecked() {
		val testItem = Normal(CharacterCreationRobots.standardProficiencyList[0])
		val toTest = DndProficiencyViewModel(testItem, concurrency)

		assert(toTest.enabled)
		assert(!toTest.checked)
	}

	@Test
	fun testSelectedItemYieldsEnabledChecked() {
		val testItem = Selected(CharacterCreationRobots.standardProficiencyList[0])
		val toTest = DndProficiencyViewModel(testItem, concurrency)

		assert(toTest.enabled)
		assert(toTest.checked)
	}

	@Test
	fun testLockedItemYieldsDisabledChecked() {
		val testItem = Locked(CharacterCreationRobots.standardProficiencyList[0])
		val toTest = DndProficiencyViewModel(testItem, concurrency)

		assert(!toTest.enabled)
		assert(toTest.checked)
	}

	@Test
	fun testSelectionEmits() {
		val testItem = Locked(CharacterCreationRobots.standardProficiencyList[0])
		val toTest = DndProficiencyViewModel(testItem, concurrency)
		val testObserver = TestObserver<Boolean>()
		toTest.selection.subscribe(testObserver)

		toTest.changeSelection(true)

		assert(testObserver.valueCount() == 1)
		testObserver.assertValue { true }
	}

	@Test
	fun testDuplicateSelectionsDoNotEmit() {
		val testItem = Locked(CharacterCreationRobots.standardProficiencyList[0])
		val toTest = DndProficiencyViewModel(testItem, concurrency)
		val testObserver = TestObserver<Boolean>()
		toTest.selection.subscribe(testObserver)

		toTest.changeSelection(false)
		toTest.changeSelection(false)

		assert(testObserver.valueCount() == 1)
		testObserver.assertValue { !it }
	}

	@Test
	fun testExternalStateChangeEmits() {
		val testInitial = Normal(CharacterCreationRobots.standardProficiencyList[0])
		val toTest = DndProficiencyViewModel(testInitial, concurrency)
		val testObserver = TestObserver<DndProficiencyViewModel>()
		toTest.changes.subscribe(testObserver)

		toTest.state = Disabled(testInitial.item)
		toTest.state = Normal(testInitial.item)

		assert(testObserver.valueCount() == 2)
	}

}