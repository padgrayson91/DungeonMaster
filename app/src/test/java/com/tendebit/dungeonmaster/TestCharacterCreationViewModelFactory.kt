package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation.feature.DndRace
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.SelectionRequirement
import com.tendebit.dungeonmaster.charactercreation.viewpager.CLASS_SELECTION
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationViewModelFactory
import com.tendebit.dungeonmaster.charactercreation.viewpager.PROFICIENCY_SELECTION_PREFIX
import com.tendebit.dungeonmaster.charactercreation.viewpager.RACE_SELECTION
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import org.junit.Test

@Suppress("UNCHECKED_CAST")
class TestCharacterCreationViewModelFactory {

	@Test
	fun testClassOptionsRequirementYieldsEmptyClassSelection() {
		val toTest = CharacterCreationViewModelFactory()

		val page = toTest.viewModelFor(DndClassOptionsRequirement(emptyList()))

		assert(page?.id == CLASS_SELECTION)
		assert((page as SelectionRequirement<DndClass>).choices.isEmpty())
	}

	@Test
	fun testClassRequirementYieldsSameClassSelection() {
		val toTest = CharacterCreationViewModelFactory()

		val page = toTest.viewModelFor(DndClassOptionsRequirement(emptyList()))
		val otherPage = toTest.viewModelFor (DndClassRequirement(null, emptyList()))

		assert(page == otherPage)
	}

	@Test
	fun testRaceOptionsRequirementYieldsEmptyRaceSelection() {
		val toTest = CharacterCreationViewModelFactory()

		val page = toTest.viewModelFor(DndRaceOptionsRequirement(emptyList()))

		assert(page?.id == RACE_SELECTION)
		assert((page as SelectionRequirement<DndRace>).choices.isEmpty())
	}

	@Test
	fun tesRaceRequirementYieldsSameRaceSelection() {
		val toTest = CharacterCreationViewModelFactory()

		val page = toTest.viewModelFor(DndRaceOptionsRequirement(emptyList()))
		val otherPage = toTest.viewModelFor(DndRaceRequirement(null, emptyList()))

		assert(page == otherPage)
	}

	@Test
	fun testProficiencyRequirementYieldsProficiencyPage() {
		val toTest = CharacterCreationViewModelFactory()

		val page = toTest.viewModelFor(DndProficiencyRequirement(null, CharacterCreationRobots.standardProficiencyGroupList[0]))

		assert(page != null && page.id.startsWith(PROFICIENCY_SELECTION_PREFIX))
	}

	@Test
	fun testProficienciesInSameGroupSharePage() {
		val toTest = CharacterCreationViewModelFactory()

		val page = toTest.viewModelFor(DndProficiencyRequirement(null, CharacterCreationRobots.standardProficiencyGroupList[0]))
		val page2 = toTest.viewModelFor(DndProficiencyRequirement(null, CharacterCreationRobots.standardProficiencyGroupList[0]))
		val page3 = toTest.viewModelFor(DndProficiencyRequirement(null, CharacterCreationRobots.standardProficiencyGroupList[0]))

		assert(page == page2 && page2 == page3)
	}

	@Test
	fun testProficienciesInDifferentGroupsGetDifferentPages() {
		val toTest = CharacterCreationViewModelFactory()

		val page = toTest.viewModelFor(DndProficiencyRequirement(null, CharacterCreationRobots.standardProficiencyGroupList[0]))
		val page2 = toTest.viewModelFor(DndProficiencyRequirement(null, CharacterCreationRobots.standardProficiencyGroupList[0]))
		val page3 = toTest.viewModelFor(DndProficiencyRequirement(null, CharacterCreationRobots.standardProficiencyGroupList[1]))

		assert(page == page2 && page2 != page3)
	}

}
