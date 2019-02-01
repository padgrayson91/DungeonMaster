package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiency
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement
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
		val testObserver = TestObserver<Requirement.Status>()
		toTest.statusChanges.subscribe(testObserver)

		toTest.update(DndProficiency("Athletics", "example.com/athletics"))

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

		toTest.update(DndProficiency("Lute", "example.com/lute"))

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
		toTest.statusChanges.subscribe(testObserver)

		toTest.update(DndProficiency("Athletics", "example.com/athletics"))
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

		toTest.update(DndProficiency("Athletics", "example.com/athletics"))
		toTest.revoke()

		assert(toTest.item == DndProficiency("Athletics", "example.com/athletics"))
	}

}
