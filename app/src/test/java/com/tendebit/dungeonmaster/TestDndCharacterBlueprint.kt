package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.feature.DndCharacterBlueprint
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndProficiencyOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import com.tendebit.dungeonmaster.testhelpers.ValueRobot
import io.reactivex.Observer
import io.reactivex.observers.TestObserver
import org.junit.Test
import kotlin.reflect.KClass

class TestDndCharacterBlueprint {

	@Test
	fun testInitialRequirements() {
		val toTest = DndCharacterBlueprint()
		val testObserver = TestObserver<List<Requirement<*>>>()
		toTest.requirements.subscribe(testObserver)

		testObserver.assertValueCount(1) // should only emit once
		testObserver.assertNotTerminated()
		testObserver.assertValue {requirements ->
			requirements.size == 2 &&
			requirements.find { it is DndClassOptionsRequirement } != null &&
			requirements.find { it is DndRaceOptionsRequirement } != null
		}
	}

	@Test
	fun testStopsEmittingWhenDestroyed() {
		val toTest = DndCharacterBlueprint()
		val testObserver = TestObserver<List<Requirement<*>>>()
		toTest.requirements.subscribe(testObserver)

		toTest.destroy()

		testObserver.assertTerminated()
	}

	@Test
	fun testRaceRequirementPresentAfterOptionsLoad() {
		val toTest = DndCharacterBlueprint()
		val testObserver = TestObserver<List<Requirement<*>>>()
		awaitRequirement(DndRaceRequirement::class, toTest, testObserver)

		testObserver.assertTerminated()
	}

	@Test
	fun testClassRequirementPresentAfterRaceSelected() {
		val toTest = DndCharacterBlueprint()
		val testObserver = TestObserver<List<Requirement<*>>>()
		awaitRequirement(DndClassRequirement::class, toTest, testObserver)

		testObserver.assertTerminated()
	}

	@Test
	fun testProficiencyOptionsRequirementPresentAfterClassSelected() {
		val toTest = DndCharacterBlueprint()
		val testObserver = TestObserver<List<Requirement<*>>>()
		awaitRequirement(DndProficiencyOptionsRequirement::class, toTest, testObserver)

		testObserver.assertTerminated()
	}

	@Test
	fun testProficiencyRequirementPresentAfterOptionsProvided() {
		val toTest = DndCharacterBlueprint()
		val testObserver = TestObserver<List<Requirement<*>>>()
		awaitRequirement(DndProficiencyOptionsRequirement::class, toTest, testObserver)

		testObserver.assertTerminated()
	}

	private fun awaitRequirement(ofType: KClass<*>, toTest: DndCharacterBlueprint, observer: Observer<List<Requirement<*>>>) {
		toTest.requirements.subscribe(observer)
		toTest.requirements.subscribe {
			for (requirement in it) {
				if (requirement::class == ofType) {
					toTest.destroy()
					break
				}
				if (requirement.status != Requirement.Status.FULFILLED) {
					CharacterCreationRobots.runRobotForRequirement(requirement, ValueRobot.TestingLevel.STANDARD)
				}
			}
		}
	}

}
