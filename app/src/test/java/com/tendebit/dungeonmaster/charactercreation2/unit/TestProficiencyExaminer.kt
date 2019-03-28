package com.tendebit.dungeonmaster.charactercreation2.unit

import com.tendebit.dungeonmaster.charactercreation2.feature.DndCharacterCreationState
import com.tendebit.dungeonmaster.charactercreation2.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiency
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation2.feature.DndRace
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencyExaminer
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation2.feature.ProficiencySource
import org.junit.Test

class TestProficiencyExaminer {

	@Test
	fun testExaminerYieldsNothingWhenNothingIsProvided() {
		val toTest = DndProficiencyExaminer()
		assert(toTest.examine(DndCharacterCreationState()).isEmpty())
	}

	@Test
	fun testExaminerYieldsCorrectNumberOfFulfillmentForSingleGroup() {
		val toTest = DndProficiencyExaminer()
		val testState = DndCharacterCreationState()
		testState.character.characterClass = DndClass("Monk", "example.com/monk")
		testState.character.race = DndRace("Halfling", "example.com/halfling")
		testState.proficiencySources[ProficiencySource.CLASS]  = arrayListOf(DndProficiencyGroup(listOf(
				DndProficiency("Athletics", "example.com"),
				DndProficiency("Brewer's Supplies", "another.com"),
				DndProficiency("Stealth", "third.com")), ArrayList(), 2))

		assert(toTest.examine(testState).size == 2)
	}

	@Test
	fun testExaminerYieldsCorrectNumberOfFulfillmentForMultipleGroup() {
		val toTest = DndProficiencyExaminer()
		val testState = DndCharacterCreationState()
		val testGroupA = DndProficiencyGroup(listOf(
				DndProficiency("Athletics", "example.com"),
				DndProficiency("Brewer's Supplies", "another.com"),
				DndProficiency("Stealth", "third.com")), ArrayList(), 2)
		val testGroupB = DndProficiencyGroup(listOf(
				DndProficiency("Athletics", "example.com"),
				DndProficiency("Brewer's Supplies", "another.com"),
				DndProficiency("Stealth", "third.com")), ArrayList(), 2)

		testState.character.characterClass = DndClass("Barbarian", "example.com/barbarian")
		testState.character.race = DndRace("Halfling", "example.com/halfling")
		testState.proficiencySources[ProficiencySource.CLASS] = arrayListOf(testGroupA, testGroupB)


		assert(toTest.examine(testState).filter { (it.requirement is DndProficiencyRequirement) && (it.requirement as DndProficiencyRequirement).fromGroup == testGroupA }.size == 2)
		assert(toTest.examine(testState).filter { (it.requirement is DndProficiencyRequirement) && (it.requirement as DndProficiencyRequirement).fromGroup == testGroupB }.size == 2)
	}

	@Test
	fun testExaminerYieldsCorrectNumberOfFulfillmentForMultipleGroupAfterSelection() {
		val toTest = DndProficiencyExaminer()
		val testState = DndCharacterCreationState()
		val testGroupA = DndProficiencyGroup(listOf(
				DndProficiency("Athletics", "example.com"),
				DndProficiency("Brewer's Supplies", "another.com"),
				DndProficiency("Stealth", "third.com")), ArrayList(), 2)
		val testGroupB = DndProficiencyGroup(listOf(
				DndProficiency("Athletics", "example.com"),
				DndProficiency("Brewer's Supplies", "another.com"),
				DndProficiency("Stealth", "third.com")), arrayListOf(DndProficiency("Brewer's Supplies", "another.com")), 2)

		testState.character.characterClass = DndClass("Wizard", "example.com/wizard")
		testState.character.race = DndRace("Halfling", "example.com/halfling")
		testState.proficiencySources[ProficiencySource.CLASS] = arrayListOf(testGroupA, testGroupB)

		val groupBFulfillment = toTest.examine(testState).filter { (it.requirement is DndProficiencyRequirement) && (it.requirement as DndProficiencyRequirement).fromGroup == testGroupB }
		// mark the one requirement with an item as fulfilled
		groupBFulfillment.find { it.requirement.item != null }?.applyToState(testState)
		val groupAFulfillment = toTest.examine(testState).filter { (it.requirement is DndProficiencyRequirement) && (it.requirement as DndProficiencyRequirement).fromGroup == testGroupA }

		assert(testState.character.proficiencies.size == 1)
		assert(groupBFulfillment.size == 2) {
			"Expected 2 requirements, but got $groupBFulfillment"
		}
		assert(groupAFulfillment.size == 3) {
			"Expected 3 requirements, but got $groupAFulfillment"
		}
	}

}
