package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.SelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.feature.DndRace
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceRequirement
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationViewModel2
import com.tendebit.dungeonmaster.charactercreation.viewpager.DndRaceSelectionPageFactory
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import org.junit.Test

class TestDndRaceSelectionPageFactory {

	@Test
	fun testRaceOptionsRequirementProducesPage() {
		val toTest = DndRaceSelectionPageFactory()
		val testRequirement = DndRaceOptionsRequirement(arrayListOf())

		val page = toTest.pageFor(testRequirement)

		assert(page?.type == CharacterCreationViewModel2.PageType.RACE_SELECTION)
	}

	@Test
	fun testRaceRequirementProducesPage() {
		val toTest = DndRaceSelectionPageFactory()
		val testRequirement = DndRaceRequirement(null, arrayListOf())

		val page = toTest.pageFor(testRequirement)

		assert(page?.type == CharacterCreationViewModel2.PageType.RACE_SELECTION)
	}

	@Test
	fun testClassOptionsRequirementProducesNothing() {
		val toTest = DndRaceSelectionPageFactory()
		val testRequirement = DndClassOptionsRequirement(arrayListOf())

		val page = toTest.pageFor(testRequirement)

		assert(page == null)
	}

	@Test
	fun testOptionsFollowedBySelectionProduceSamePage() {
		val toTest = DndRaceSelectionPageFactory()
		val testRequirement1 = DndRaceOptionsRequirement(arrayListOf())

		val page1 = toTest.pageFor(testRequirement1)

		val testRequirement2 = DndRaceRequirement(null, arrayListOf())

		val page2 = toTest.pageFor(testRequirement2)

		assert(page1 == page2)
	}

	@Test
	fun testAppliesDataToViewModel() {
		val testRequirement = DndRaceRequirement(CharacterCreationRobots.standardRaceList[0], CharacterCreationRobots.standardRaceList)
		val toTest = DndRaceSelectionPageFactory()
		val viewModel = SelectionViewModel<DndRace>()

		val page = toTest.pageFor(testRequirement)
		toTest.applyData(page!!.id, viewModel)

		assert(viewModel.options == testRequirement.choices)
		assert(viewModel.selection == testRequirement.item)
	}

}
