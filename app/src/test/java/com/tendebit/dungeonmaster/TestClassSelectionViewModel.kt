package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.ClassSelectionViewModel2
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestClassSelectionViewModel {

	@Test
	fun testViewModelHasNoSelectionWithEmptyRequirement() {
		val testObserver = TestObserver<Any>()
		val testRequirement = DndClassRequirement(null, emptyList())
		val toTest = ClassSelectionViewModel2(testRequirement)

		toTest.selectionChanges.subscribe(testObserver)

		testObserver.assertEmpty()
	}

	@Test
	fun testViewModelEmitsInitialSelectionFromRequirement() {
		val testObserver = TestObserver<DndClass>()
		val testClass = DndClass("Rogue", "example.com/rogue")
		val testRequirement = DndClassRequirement(testClass, listOf(testClass))
		val toTest = ClassSelectionViewModel2(testRequirement)

		toTest.selectionChanges.subscribe(testObserver)

		testObserver.assertValueCount(1)
		testObserver.assertValue { it == testClass }
	}

	@Test
	fun testViewModelEmitsValidSelectionWhenSelected() {
		val testObserver = TestObserver<DndClass>()
		val testClass = DndClass("Rogue", "example.com/rogue")
		val testRequirement = DndClassRequirement(null, listOf(testClass))
		val toTest = ClassSelectionViewModel2(testRequirement)

		toTest.selectionChanges.subscribe(testObserver)
		toTest.select(testClass)

		testObserver.assertValueCount(1)
		testObserver.assertValue { it == testClass }
	}

	@Test
	fun testViewModelDoesNotEmitInvalidSelection() {
		val testObserver = TestObserver<DndClass>()
		val testClass = DndClass("Rogue", "example.com/rogue")
		val invalidClass = DndClass("God", "example.com/god")
		val testRequirement = DndClassRequirement(null, listOf(testClass))
		val toTest = ClassSelectionViewModel2(testRequirement)

		toTest.selectionChanges.subscribe(testObserver)
		toTest.select(invalidClass)

		testObserver.assertEmpty()
	}

	@Test
	fun testViewModelEmitsNewOptionsWhenRequirementChanges() {
		val testObserver = TestObserver<List<DndClass>>()
		val testOptions = listOf(
				DndClass("Rogue", "example.com/rogue"),
				DndClass("Barbarian", "example.com/barbarian"))
		val otherOptions = listOf(
				DndClass("Wizard", "example.com/wizard"),
				DndClass("Fighter", "example.com/fighter")
								 )
		val testRequirement = DndClassRequirement(null, testOptions)
		val otherRequirement = DndClassRequirement(null, otherOptions)
		val toTest = ClassSelectionViewModel2(testRequirement)

		toTest.optionChanges.subscribe(testObserver)
		toTest.requirement = otherRequirement

		testObserver.assertValueCount(2)
		testObserver.assertValueAt(1) { it == otherOptions }
	}

	@Test
	fun testViewModelDoesNotEmitOptionsIfTheyMatch() {
		val testObserver = TestObserver<List<DndClass>>()
		val testOptions = listOf(
				DndClass("Rogue", "example.com/rogue"),
				DndClass("Barbarian", "example.com/barbarian"))
		val otherOptions = listOf(
				DndClass("Rogue", "example.com/rogue"),
				DndClass("Barbarian", "example.com/barbarian"))
		val testRequirement = DndClassRequirement(null, testOptions)
		val otherRequirement = DndClassRequirement(null, otherOptions)
		val toTest = ClassSelectionViewModel2(testRequirement)

		toTest.optionChanges.subscribe(testObserver)
		toTest.requirement = otherRequirement

		testObserver.assertValueCount(1)
	}

	@Test
	fun testViewModelEmitsNewSelectionWhenNewRequirementHasDifferentItem() {
		val testObserver = TestObserver<DndClass>()
		val testClass = DndClass("Rogue", "example.com/rogue")
		val otherClass = DndClass("Wizard", "example.com/wizard")
		val testRequirement = DndClassRequirement(testClass, listOf(testClass))
		val otherRequirement = DndClassRequirement(otherClass, listOf(otherClass))
		val toTest = ClassSelectionViewModel2(testRequirement)

		toTest.selectionChanges.subscribe(testObserver)
		toTest.requirement = otherRequirement

		testObserver.assertValueCount(2)
		testObserver.assertValueAt(1) { it == otherClass }
	}

	@Test
	fun testViewModelDoesNotEmitSelectionForNewRequirementIfItMatchesPrevious() {
		val testObserver = TestObserver<DndClass>()
		val testClass = DndClass("Rogue", "example.com/rogue")
		val otherClass = DndClass("Rogue", "example.com/rogue")
		val testRequirement = DndClassRequirement(testClass, listOf(testClass))
		val otherRequirement = DndClassRequirement(otherClass, listOf(otherClass))
		val toTest = ClassSelectionViewModel2(testRequirement)

		toTest.selectionChanges.subscribe(testObserver)
		toTest.requirement = otherRequirement

		testObserver.assertValueCount(1)
	}

}
