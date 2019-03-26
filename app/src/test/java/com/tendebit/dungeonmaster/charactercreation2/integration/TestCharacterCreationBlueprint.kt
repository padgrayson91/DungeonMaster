package com.tendebit.dungeonmaster.charactercreation2.integration

import com.tendebit.dungeonmaster.charactercreation2.feature.CharacterClassExaminer
import com.tendebit.dungeonmaster.charactercreation2.feature.CharacterPrerequisiteExaminer
import com.tendebit.dungeonmaster.charactercreation2.feature.CharacterProficiencyExaminer
import com.tendebit.dungeonmaster.charactercreation2.feature.CharacterProficiencyOptionsExaminer
import com.tendebit.dungeonmaster.charactercreation2.feature.CharacterRaceExaminer
import com.tendebit.dungeonmaster.charactercreation2.feature.DndCharacterCreationState
import com.tendebit.dungeonmaster.charactercreation2.feature.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation2.feature.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencyOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation2.feature.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation2.feature.DndRaceRequirement
import com.tendebit.dungeonmaster.core.blueprint.Blueprint
import com.tendebit.dungeonmaster.core.blueprint.Delta
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestCharacterCreationBlueprint {

	private val examiners = listOf(
			CharacterPrerequisiteExaminer(),
			CharacterClassExaminer(),
			CharacterRaceExaminer(),
			CharacterProficiencyOptionsExaminer(),
			CharacterProficiencyExaminer())

	@Test
	fun testStartsWithRequirementForClassListAndRaceList() {
		val toTest = Blueprint(examiners, DndCharacterCreationState())
		val testObserver = TestObserver<List<Delta<Requirement<*>>>>()

		toTest.requirements.subscribe(testObserver)

		testObserver.assertValueCount(1)
		testObserver.assertValueAt(0) { it.size == 2 }
		val deltas = testObserver.values()[0]
		assert(deltas[0].item is DndClassOptionsRequirement)
		assert(deltas[1].item is DndRaceOptionsRequirement)
	}

	@Test
	fun testAfterOnlyRaceListProvidedNoNewRequirementsEmit() {
		val toTest = Blueprint(examiners, DndCharacterCreationState())
		val testObserver = TestObserver<List<Delta<Requirement<*>>>>()

		toTest.requirements.subscribe(testObserver)

		CharacterCreationRobots.runRobotForRequirement(testObserver.values()[0][1].item as DndRaceOptionsRequirement)

		testObserver.assertValueCount(2)
		testObserver.assertValueAt(1) { it.size == 2 }
	}

	@Test
	fun testAfterOnlyClassListProvidedClassRequirementEmits() {
		val toTest = Blueprint(examiners, DndCharacterCreationState())
		val testObserver = TestObserver<List<Delta<Requirement<*>>>>()

		toTest.requirements.subscribe(testObserver)

		CharacterCreationRobots.runRobotForRequirement(testObserver.values()[0][0].item as DndClassOptionsRequirement)

		testObserver.assertValueCount(2)
		val deltas = testObserver.values()[testObserver.valueCount() - 1]
		assert(deltas.size == 3) { "Got $deltas"}
		val requirements = deltas.map { it.item }
		assert(requirements.find { it is DndClassRequirement } != null)
	}

	@Test
	fun testAfterRaceAndClassListsProvidedClassRequirementsEmit() {
		val toTest = Blueprint(examiners, DndCharacterCreationState())
		val testObserver = TestObserver<List<Delta<Requirement<*>>>>()

		toTest.requirements.subscribe(testObserver)

		CharacterCreationRobots.runRobotForRequirement(testObserver.values()[0][0].item as DndClassOptionsRequirement)
		CharacterCreationRobots.runRobotForRequirement(testObserver.values()[0][1].item as DndRaceOptionsRequirement)

		testObserver.assertValueCount(3)
		val deltas = testObserver.values()[testObserver.valueCount() - 1]
		assert(deltas.size == 3) { "Got $deltas"}
		val requirements = deltas.map { it.item }
		assert(requirements.find { it is DndClassRequirement } != null)
	}

	@Test
	fun testAfterClassSelectedRaceAndProficiencyOptionsRequirementsEmit() {
		val toTest = Blueprint(examiners, DndCharacterCreationState())
		val testObserver = TestObserver<List<Delta<Requirement<*>>>>()

		toTest.requirements.subscribe(testObserver)

		CharacterCreationRobots.runRobotForRequirement(testObserver.values()[0][0].item as DndClassOptionsRequirement)
		CharacterCreationRobots.runRobotForRequirement(testObserver.values()[0][1].item as DndRaceOptionsRequirement)
		CharacterCreationRobots.runRobotForRequirement(testObserver.values()[1][2].item as DndClassRequirement)

		testObserver.assertValueCount(4)
		val deltas = testObserver.values()[testObserver.valueCount() - 1]
		assert(deltas.size == 5) { "Got $deltas"}
		val requirements = deltas.map { it.item }
		assert(requirements.find { it is DndRaceRequirement } != null)
		assert(requirements.find { it is DndProficiencyOptionsRequirement } != null)
	}

	@Test
	fun testAfterRaceSelectedAndProficiencyOptionsProvidedProficiencyRequirementsEmit() {
		val toTest = Blueprint(examiners, DndCharacterCreationState())
		val testObserver = TestObserver<List<Delta<Requirement<*>>>>()

		toTest.requirements.subscribe(testObserver)

		CharacterCreationRobots.runRobotForRequirement(testObserver.values()[0][0].item as DndClassOptionsRequirement)
		CharacterCreationRobots.runRobotForRequirement(testObserver.values()[0][1].item as DndRaceOptionsRequirement)
		CharacterCreationRobots.runRobotForRequirement(testObserver.values()[1][2].item as DndClassRequirement)
		CharacterCreationRobots.runRobotForRequirement(testObserver.values()[3][3].item as DndRaceRequirement)
		CharacterCreationRobots.runRobotForRequirement(testObserver.values()[3][4].item as DndProficiencyOptionsRequirement)

		testObserver.assertValueCount(6)
		val deltas = testObserver.values()[testObserver.valueCount() - 1]
		val requirements = deltas.map { it.item }
		assert(requirements.find { it is DndProficiencyRequirement } != null)
	}

	@Test
	fun testAfterAllRequirementsFulfilledCharacterHasRaceClassAndProficiencies() {
		val toTest = Blueprint(examiners, DndCharacterCreationState())
		val testObserver = TestObserver<List<Delta<Requirement<*>>>>()

		toTest.requirements.subscribe(testObserver)

		while (testObserver.values().last().any { it.item?.status == Requirement.Status.NOT_FULFILLED }) {
			val unfulfilledRequirement = testObserver.values().last().first { it.item?.status == Requirement.Status.NOT_FULFILLED }.item!!
			CharacterCreationRobots.runRobotForRequirement(unfulfilledRequirement)
		}

		assert(testObserver.values().size >= 4) { "Had ${testObserver.values().last()}"}

		val character = toTest.state.character

		assert(character.race != null) { "Had $character"}
		assert(character.characterClass != null) { "Had $character"}
		assert(character.proficiencies.isNotEmpty()) { "Had $character"}
	}

}
