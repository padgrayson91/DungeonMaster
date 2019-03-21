package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation2.feature.DndCharacterCreationState
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiency
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencyFulfillment
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencyRequirement
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

class TestProficiencyFulfillment {

	@Test
	fun testStateUpdatedOnFulfilled() {
		val testState = DndCharacterCreationState()
		val mockedGroup = Mockito.mock(DndProficiencyGroup::class.java)
		val mockedRequirement = Mockito.mock(DndProficiencyRequirement::class.java)
		val testProficiency = DndProficiency("Athletics", "example.com/athletics")
		val testSelection = DndProficiencySelection(testProficiency, mockedGroup)
		whenever(mockedGroup.availableOptions).thenReturn(listOf(testProficiency))
		whenever(mockedRequirement.item).thenReturn(testSelection)
		whenever(mockedRequirement.status).thenReturn(Requirement.Status.FULFILLED)
		val toTest = DndProficiencyFulfillment(mockedRequirement)

		toTest.applyToState(testState)

		assert(testState.character.proficiencies.contains(testSelection))
	}

	@Test
	fun testStateEmptyBeforeFulfilled() {
		val testState = DndCharacterCreationState()
		val mockedGroup = Mockito.mock(DndProficiencyGroup::class.java)
		val mockedRequirement = Mockito.mock(DndProficiencyRequirement::class.java)
		val testProficiency = DndProficiency("Athletics", "example.com/athletics")
		val testSelection = DndProficiencySelection(testProficiency, mockedGroup)
		whenever(mockedRequirement.item).thenReturn(testSelection)
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
		val testSelection = DndProficiencySelection(testProficiency, mockedGroup)
		whenever(mockedRequirement.item).thenReturn(testSelection)
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
