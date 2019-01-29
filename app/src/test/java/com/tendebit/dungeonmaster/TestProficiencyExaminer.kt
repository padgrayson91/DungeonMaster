package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.model.DndCharacterCreationState
import com.tendebit.dungeonmaster.charactercreation.model.DndProficiency
import com.tendebit.dungeonmaster.charactercreation.model.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.model.examiner.CharacterProficiencyExaminer
import com.tendebit.dungeonmaster.charactercreation.model.requirement.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation.model.requirement.Requirement
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory
import org.junit.Test

class TestProficiencyExaminer {

	@Test
	fun testExaminerYieldsNothingWhenNothingIsProvided() {
		val toTest = CharacterProficiencyExaminer()
		assert(toTest.getFulfillmentsForState(DndCharacterCreationState()).isEmpty())
	}

	@Test
	fun testExaminerYieldsNothingIfNoClassIsSelected() {
		val toTest = CharacterProficiencyExaminer()
		val testState = DndCharacterCreationState()
		testState.character.race = CharacterRaceDirectory()
		testState.proficiencyOptions.add(DndProficiencyGroup(listOf(DndProficiency("Athletics", "example.com")), ArrayList(), 1))

		assert(toTest.getFulfillmentsForState(testState).isEmpty())
	}

	@Test
	fun testExaminerYieldsCorrectNumberOfFulfillmentForSingleGroup() {
		val toTest = CharacterProficiencyExaminer()
		val testState = DndCharacterCreationState()
		testState.character.characterClass = CharacterClassDirectory()
		testState.character.race = CharacterRaceDirectory()
		testState.proficiencyOptions.add(DndProficiencyGroup(listOf(
				DndProficiency("Athletics", "example.com"),
				DndProficiency("Brewer's Supplies", "another.com"),
				DndProficiency("Stealth", "athird.com")), ArrayList(), 2))

		assert(toTest.getFulfillmentsForState(testState).size == 2)
	}

	@Test
	fun testExaminerYieldsCorrectNumberOfFulfillmentForMultipleGroup() {
		val toTest = CharacterProficiencyExaminer()
		val testState = DndCharacterCreationState()
		val testGroupA = DndProficiencyGroup(listOf(
				DndProficiency("Athletics", "example.com"),
				DndProficiency("Brewer's Supplies", "another.com"),
				DndProficiency("Stealth", "athird.com")), ArrayList(), 2)
		val testGroupB = DndProficiencyGroup(listOf(
				DndProficiency("Athletics", "example.com"),
				DndProficiency("Brewer's Supplies", "another.com"),
				DndProficiency("Stealth", "athird.com")), ArrayList(), 2)

		testState.character.characterClass = CharacterClassDirectory()
		testState.character.race = CharacterRaceDirectory()
		testState.proficiencyOptions.add(testGroupA)
		testState.proficiencyOptions.add(testGroupB)


		assert(toTest.getFulfillmentsForState(testState).filter { (it.requirement is DndProficiencyRequirement) && (it.requirement as DndProficiencyRequirement).fromGroup == testGroupA }.size == 2)
		assert(toTest.getFulfillmentsForState(testState).filter { (it.requirement is DndProficiencyRequirement) && (it.requirement as DndProficiencyRequirement).fromGroup == testGroupB }.size == 2)


	}

	@Test
	fun testExaminerYieldsCorrectNumberOfFulfillmentForMultipleGroupAfterSelection() {
		val toTest = CharacterProficiencyExaminer()
		val testState = DndCharacterCreationState()
		val testGroupA = DndProficiencyGroup(listOf(
				DndProficiency("Athletics", "example.com"),
				DndProficiency("Brewer's Supplies", "another.com"),
				DndProficiency("Stealth", "athird.com")), ArrayList(), 2)
		val testGroupB = DndProficiencyGroup(listOf(
				DndProficiency("Athletics", "example.com"),
				DndProficiency("Brewer's Supplies", "another.com"),
				DndProficiency("Stealth", "athird.com")), arrayListOf(DndProficiency("Brewer's Supplies", "another.com")), 2)

		testState.character.characterClass = CharacterClassDirectory()
		testState.character.race = CharacterRaceDirectory()
		testState.proficiencyOptions.add(testGroupA)
		testState.proficiencyOptions.add(testGroupB)

		val groupBFulfillment = toTest.getFulfillmentsForState(testState).filter { (it.requirement is DndProficiencyRequirement) && (it.requirement as DndProficiencyRequirement).fromGroup == testGroupB }
		// mark the one requirement with an item as fulfilled
		groupBFulfillment.find { it.requirement.item != null }?.let {
			it.requirement.status = Requirement.Status.FULFILLED
			it.applyToState(testState)
		}
		val groupAFulfillment = toTest.getFulfillmentsForState(testState).filter { (it.requirement is DndProficiencyRequirement) && (it.requirement as DndProficiencyRequirement).fromGroup == testGroupA }

		assert(testState.character.proficiencies.size == 1)
		assert(groupBFulfillment.size == 2) {
			"Expected 2 requirements, but got $groupBFulfillment"
		}
		assert(groupAFulfillment.size == 3) {
			"Expected 3 requirements, but got $groupAFulfillment"
		}

	}

}
