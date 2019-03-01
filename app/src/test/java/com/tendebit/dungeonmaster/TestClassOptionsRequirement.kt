package com.tendebit.dungeonmaster


import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation.feature.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestClassOptionsRequirement {

	@Test
	fun testNoStatusChangeWhenEmpty() {
		val toTest = DndClassOptionsRequirement(emptyList())
		val testObserver = TestObserver<Requirement.Status>()
		toTest.statusChanges.subscribe(testObserver)

		testObserver.assertEmpty()
	}

	@Test
	fun testStatusChangesOnFulfillOptions() {
		val testOptions = listOf(
				DndClass("Rogue", "example.com/rogue"),
				DndClass("Wizard", "example.com/wizard"))
		val toTest = DndClassOptionsRequirement(emptyList())
		val testObserver = TestObserver<Requirement.Status>()
		toTest.statusChanges.subscribe(testObserver)

		toTest.update(testOptions)

		testObserver.assertValue(Requirement.Status.FULFILLED)
	}

	@Test
	fun testStatusChangesOnOptionsRevoked() {
		val testOptions = listOf(
				DndClass("Rogue", "example.com/rogue"),
				DndClass("Wizard", "example.com/wizard"))
		val toTest = DndClassOptionsRequirement(emptyList())
		val testObserver = TestObserver<Requirement.Status>()
		toTest.statusChanges.subscribe(testObserver)

		toTest.update(testOptions)
		toTest.revoke()

		testObserver.assertValueCount(2)
		testObserver.assertValueAt(0) { it == Requirement.Status.FULFILLED }
		testObserver.assertValueAt(1) { it == Requirement.Status.NOT_FULFILLED }
	}

	@Test
	fun testStatusNotFulfilledWithEmptyList() {
		val toTest = DndClassOptionsRequirement(emptyList())
		val testObserver = TestObserver<Requirement.Status>()
		toTest.statusChanges.subscribe(testObserver)
		toTest.update(emptyList())

		testObserver.assertValueCount(1)
		testObserver.assertValueAt(testObserver.valueCount() - 1) { it == Requirement.Status.NOT_FULFILLED }
	}

	@Test
	fun testStatusFulfilledWithInitialNonEmptyList() {
		val testOptions = listOf(
				DndClass("Rogue", "example.com/rogue"),
				DndClass("Wizard", "example.com/wizard"))
		val toTest = DndClassOptionsRequirement(testOptions)

		assert(toTest.status == Requirement.Status.FULFILLED)
	}

	@Test
	fun testStatusNotFulfilledWithInitialEmptyList() {
		val toTest = DndClassOptionsRequirement(emptyList())

		assert(toTest.status == Requirement.Status.NOT_FULFILLED)
	}

}
