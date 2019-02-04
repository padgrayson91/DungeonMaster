package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.feature.DndCharacterBlueprint
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import com.tendebit.dungeonmaster.testhelpers.ValueRobot
import io.reactivex.observers.TestObserver
import org.junit.Test

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
		toTest.requirements.subscribe(testObserver)
		toTest.requirements.subscribe {
			for (requirement in it) {
				if (requirement is DndRaceRequirement) {
					toTest.destroy()
				}
				if (requirement.status != Requirement.Status.FULFILLED) {
					CharacterCreationRobots.runRobotForRequirement(requirement, ValueRobot.TestingLevel.STANDARD)
				}
			}
		}

		testObserver.assertTerminated()
	}

}
