package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.model.DndCharacterCreationState
import com.tendebit.dungeonmaster.charactercreation.model.DndProficiency
import com.tendebit.dungeonmaster.charactercreation.model.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.model.fulfillment.DndProficiencyFulfillment
import com.tendebit.dungeonmaster.charactercreation.model.requirement.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation.model.requirement.Requirement
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

class TestProficiencyFulfillment {

	@Test
	fun testStateUpdatedOnFulfilled() {
		val testState = DndCharacterCreationState()
		val mockedRequirement = Mockito.mock(DndProficiencyRequirement::class.java)
		val testProficiency = DndProficiency("Athletics", "example.com/athletics")
		whenever(mockedRequirement.item).thenReturn(testProficiency)
		whenever(mockedRequirement.status).thenReturn(Requirement.Status.FULFILLED)
		val toTest = DndProficiencyFulfillment(mockedRequirement)

		toTest.applyToState(testState)

		assert(testState.character.proficiencies.contains(testProficiency))
	}

	@Test
	fun testStateEmptyBeforeFulfilled() {
		val testState = DndCharacterCreationState()
		val mockedRequirement = Mockito.mock(DndProficiencyRequirement::class.java)
		val testProficiency = DndProficiency("Athletics", "example.com/athletics")
		whenever(mockedRequirement.item).thenReturn(testProficiency)
		val toTest = DndProficiencyFulfillment(mockedRequirement)

		toTest.applyToState(testState)

		assert(testState.character.proficiencies.isEmpty()) { "Expected empty list but got ${testState.character.proficiencies}"}
	}

	@Test
	fun testStateUpdatedOnRevoked() {
		val testState = DndCharacterCreationState()
		val mockedRequirement = Mockito.mock(DndProficiencyRequirement::class.java)
		val mockedGroup = Mockito.mock(DndProficiencyGroup::class.java)
		val testProficiency = DndProficiency("Athletics", "example.com/athletics")
		whenever(mockedRequirement.item).thenReturn(testProficiency)
		whenever(mockedGroup.availableOptions).thenReturn(listOf(testProficiency))
		whenever(mockedRequirement.fromGroup).thenReturn(mockedGroup)
		val toTest = DndProficiencyFulfillment(mockedRequirement)

		whenever(mockedRequirement.status).thenReturn(Requirement.Status.FULFILLED)
		toTest.applyToState(testState)
		assert(testState.character.proficiencies.isNotEmpty())

		whenever(mockedRequirement.status).thenReturn(Requirement.Status.NOT_FULFILLED)
		toTest.applyToState(testState)

		assert(testState.character.proficiencies.isEmpty()) { "Expected empty list but got ${testState.character.proficiencies}"}
	}

}
