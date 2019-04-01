package com.tendebit.dungeonmaster.charactercreation2.unit

import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiency
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencyRequirement
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestProficiencyRequirement {

	@Test
	fun testStatusChangeOnFulfillProficiency() {
		val testOptions = listOf(
				DndProficiency("Athletics", "example.com/athletics"),
				DndProficiency("Brewer's Supplies", "example.com/brewers_supplies"))
		val testGroup = DndProficiencyGroup(testOptions, ArrayList(), 1)
		val toTest = DndProficiencyRequirement(null, testGroup)
		val testSelection = DndProficiencySelection(testOptions[0], testGroup)
		val testObserver = TestObserver<Requirement.Status>()
		toTest.statusChanges.subscribe(testObserver)

		toTest.update(testSelection)

		testObserver.assertNoErrors()
		testObserver.assertValue(Requirement.Status.FULFILLED)
	}

	@Test
	fun testStatusDoesNotChangeWithInvalidOption() {
		val testOptions = listOf(
				DndProficiency("Athletics", "example.com/athletics"),
				DndProficiency("Brewer's Supplies", "example.com/brewers_supplies"))
		val testGroup = DndProficiencyGroup(testOptions, ArrayList(), 1)
		val toTest = DndProficiencyRequirement(null, testGroup)
		val testObserver = TestObserver<Requirement.Status>()
		toTest.statusChanges.subscribe(testObserver)

		toTest.update(DndProficiencySelection(DndProficiency("Lute", "example.com/lute"), testGroup))

		testObserver.assertNoErrors()
		testObserver.assertEmpty()
	}

	@Test
	fun testStatusUnfulfilledWhenRevoked() {
		val testOptions = listOf(
				DndProficiency("Athletics", "example.com/athletics"),
				DndProficiency("Brewer's Supplies", "example.com/brewers_supplies"))
		val testGroup = DndProficiencyGroup(testOptions, ArrayList(), 1)
		val toTest = DndProficiencyRequirement(null, testGroup)
		val testObserver = TestObserver<Requirement.Status>()
		val testSelection = DndProficiencySelection(testOptions[0], testGroup)
		toTest.statusChanges.subscribe(testObserver)

		toTest.update(testSelection)
		toTest.revoke()

		testObserver.assertNoErrors()
		testObserver.assertValueCount(2)
		testObserver.assertValueAt(testObserver.valueCount() - 1) { it == Requirement.Status.NOT_FULFILLED }
	}

	@Test
	fun testItemStillAccessibleWhenRevoked() {
		val testOptions = listOf(
				DndProficiency("Athletics", "example.com/athletics"),
				DndProficiency("Brewer's Supplies", "example.com/brewers_supplies"))
		val testGroup = DndProficiencyGroup(testOptions, ArrayList(), 1)
		val toTest = DndProficiencyRequirement(null, testGroup)
		val testSelection = DndProficiencySelection(testOptions[0], testGroup)

		toTest.update(testSelection)
		toTest.revoke()

		assert(toTest.item?.proficiency == testOptions[0])
	}

}
