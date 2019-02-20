package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.SelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationViewModel2
import com.tendebit.dungeonmaster.charactercreation.viewpager.DndClassSelectionPageFactory
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import org.junit.Test

class TestDndClassSelectionPageFactory {

	@Test
	fun testClassOptionsRequirementProducesPage() {
		val toTest = DndClassSelectionPageFactory()
		val testRequirement = DndClassOptionsRequirement(arrayListOf())

		val page = toTest.pageFor(testRequirement)

		assert(page?.type == CharacterCreationViewModel2.PageType.CLASS_SELECTION)
	}

	@Test
	fun testClassRequirementProducesPage() {
		val toTest = DndClassSelectionPageFactory()
		val testRequirement = DndClassRequirement(null, arrayListOf())

		val page = toTest.pageFor(testRequirement)

		assert(page?.type == CharacterCreationViewModel2.PageType.CLASS_SELECTION)
	}

	@Test
	fun testRaceOptionsRequirementProducesNothing() {
		val toTest = DndClassSelectionPageFactory()
		val testRequirement = DndRaceOptionsRequirement(arrayListOf())

		val page = toTest.pageFor(testRequirement)

		assert(page == null)
	}

	@Test
	fun testAppliesDataToViewModel() {
		val testRequirement = DndClassRequirement(CharacterCreationRobots.standardClassList[0], CharacterCreationRobots.standardClassList)
		val toTest = DndClassSelectionPageFactory()
		val viewModel = SelectionViewModel<DndClass>()

		val page = toTest.pageFor(testRequirement)
		toTest.applyData(page!!.id, viewModel)

		assert(viewModel.options == testRequirement.choices)
		assert(viewModel.selection == testRequirement.item)
	}

}
