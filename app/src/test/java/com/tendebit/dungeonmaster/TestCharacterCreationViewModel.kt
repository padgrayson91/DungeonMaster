package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.feature.DndCharacterBlueprint
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceRequirement
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationViewModel2
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

class TestCharacterCreationViewModel {

	@Test
	fun testInitialStateHasNoPages() {
		val testBlueprint = DndCharacterBlueprint()
		val packager = Mockito.mock(CharacterCreationViewModel2.Packager::class.java)
		val toTest = CharacterCreationViewModel2(testBlueprint, packager)
		val testObserver = TestObserver<CharacterCreationViewModel2.PageInsertion>()

		toTest.pageAdditions.subscribe(testObserver)

		testObserver.assertEmpty()
		assert(toTest.pages.isEmpty())
	}

	@Test
	fun testInitialStateHasNoPageRemovals() {
		val testBlueprint = DndCharacterBlueprint()
		val packager = Mockito.mock(CharacterCreationViewModel2.Packager::class.java)
		val toTest = CharacterCreationViewModel2(testBlueprint, packager)
		val testObserver = TestObserver<CharacterCreationViewModel2.PageRemoval>()

		toTest.pageRemovals.subscribe(testObserver)

		testObserver.assertEmpty()
	}

	@Test
	fun testHasOnePageWhenPackagerEmitsOnePage() {
		val testBlueprint = Mockito.mock(DndCharacterBlueprint::class.java)
		val packager = Mockito.mock(CharacterCreationViewModel2.Packager::class.java)
		val testRequirement = DndClassRequirement(null, emptyList())
		whenever(packager.pageFor(testRequirement)).thenReturn(CharacterCreationViewModel2.Page(CharacterCreationViewModel2.PageType.CLASS_SELECTION, "class_selection"))
		whenever(testBlueprint.requirements).thenReturn(Observable.fromArray(listOf(testRequirement)))
		val toTest = CharacterCreationViewModel2(testBlueprint, packager)


		assert(toTest.pages.size == 1)
		assert(toTest.pages[0].type == CharacterCreationViewModel2.PageType.CLASS_SELECTION)
	}

	@Test
	fun testHasTwoPagesWhenPackagerEmitsTwoPages() {
		val testBlueprint = Mockito.mock(DndCharacterBlueprint::class.java)
		val packager = Mockito.mock(CharacterCreationViewModel2.Packager::class.java)
		val testRequirement = DndClassRequirement(null, emptyList())
		val otherRequirement = DndRaceRequirement(null, emptyList())
		whenever(testBlueprint.requirements).thenReturn(Observable.fromArray(listOf(
				testRequirement,
				otherRequirement)))
		whenever(packager.pageFor(testRequirement)).thenReturn(CharacterCreationViewModel2.Page(CharacterCreationViewModel2.PageType.CLASS_SELECTION, "class_selection"))
		whenever(packager.pageFor(otherRequirement)).thenReturn(CharacterCreationViewModel2.Page(CharacterCreationViewModel2.PageType.RACE_SELECTION, "race_selection"))


		val toTest = CharacterCreationViewModel2(testBlueprint, packager)


		assert(toTest.pages.size == 2)
		assert(toTest.pages[0].type == CharacterCreationViewModel2.PageType.CLASS_SELECTION)
		assert(toTest.pages[1].type == CharacterCreationViewModel2.PageType.RACE_SELECTION)
	}

	@Test
	fun testHasPagesForEachProficiencyGroupEmittedByPackager() {
		val testBlueprint = Mockito.mock(DndCharacterBlueprint::class.java)
		val packager = Mockito.mock(CharacterCreationViewModel2.Packager::class.java)
		val mockGroup1 = Mockito.mock(DndProficiencyGroup::class.java)
		val mockGroup2 = Mockito.mock(DndProficiencyGroup::class.java)
		val mockGroup3 = Mockito.mock(DndProficiencyGroup::class.java)
		val testGroups = listOf(mockGroup1, mockGroup2, mockGroup3)
		val testRequirements = arrayListOf<DndProficiencyRequirement>()
		testGroups.forEach {
			for (i in 0 until it.remainingChoices()) {
				val requirement = DndProficiencyRequirement(null, it)
				testRequirements.add(requirement)
				whenever(packager.pageFor(requirement))
						.thenReturn(CharacterCreationViewModel2.Page(CharacterCreationViewModel2.PageType.PROFICIENCY_SELECTION, "proficiency_selection_$i"))
			}
		}
		whenever(testBlueprint.requirements).thenReturn(Observable.fromIterable(listOf(testRequirements)))
		val toTest = CharacterCreationViewModel2(testBlueprint, packager)


		assert(toTest.pages.size == testGroups.size)
		for (page in toTest.pages) {
			assert(page.type == CharacterCreationViewModel2.PageType.PROFICIENCY_SELECTION)
		}

	}

}
