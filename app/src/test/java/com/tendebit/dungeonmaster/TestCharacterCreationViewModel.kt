package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.feature.DndCharacterBlueprint
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndProficiencyOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationViewModel2
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import io.reactivex.subjects.PublishSubject
import org.junit.Test
import org.mockito.Mockito
import java.util.concurrent.TimeUnit
import org.mockito.Mockito.`when` as whenever

class TestCharacterCreationViewModel {

	@Test
	fun testInitialStateHasNoPages() {
		val testBlueprint = DndCharacterBlueprint()
		val packager = Mockito.mock(CharacterCreationViewModel2.PageFactory::class.java)
		val toTest = CharacterCreationViewModel2(testBlueprint, packager)
		val testObserver = TestObserver<CharacterCreationViewModel2.PageInsertion>()

		toTest.pageAdditions.subscribe(testObserver)

		testObserver.assertEmpty()
		assert(toTest.pages.isEmpty())
	}

	@Test
	fun testInitialStateIsLoading() {
		val testBlueprint = DndCharacterBlueprint()
		val packager = Mockito.mock(CharacterCreationViewModel2.PageFactory::class.java)
		val toTest = CharacterCreationViewModel2(testBlueprint, packager)

		assert(toTest.isLoading)
	}

	@Test
	fun testInitialStateHasNoPageRemovals() {
		val testBlueprint = DndCharacterBlueprint()
		val packager = Mockito.mock(CharacterCreationViewModel2.PageFactory::class.java)
		val toTest = CharacterCreationViewModel2(testBlueprint, packager)
		val testObserver = TestObserver<CharacterCreationViewModel2.PageRemoval>()

		toTest.pageRemovals.subscribe(testObserver)

		testObserver.assertEmpty()
	}

	@Test
	fun testHasOnePageWhenPackagerEmitsOnePage() {
		val testBlueprint = Mockito.mock(DndCharacterBlueprint::class.java)
		val packager = Mockito.mock(CharacterCreationViewModel2.PageFactory::class.java)
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
		val packager = Mockito.mock(CharacterCreationViewModel2.PageFactory::class.java)
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
		val packager = Mockito.mock(CharacterCreationViewModel2.PageFactory::class.java)
		val mockGroup1 = Mockito.mock(DndProficiencyGroup::class.java)
		val mockGroup2 = Mockito.mock(DndProficiencyGroup::class.java)
		val mockGroup3 = Mockito.mock(DndProficiencyGroup::class.java)
		val testGroups = listOf(mockGroup1, mockGroup2, mockGroup3)
		val testRequirements = arrayListOf<DndProficiencyRequirement>()
		for (j in 0 until testGroups.size) {
			val group = testGroups[j]
			// Add 3 requirements per group
			for (i in 0 until 3) {
				val requirement = DndProficiencyRequirement(null, group)
				testRequirements.add(requirement)
				whenever(packager.pageFor(requirement)).thenReturn(CharacterCreationViewModel2.Page(CharacterCreationViewModel2.PageType.PROFICIENCY_SELECTION, "proficiency_$j"))
			}
		}
		whenever(testBlueprint.requirements).thenReturn(Observable.fromIterable(listOf(testRequirements)))
		val toTest = CharacterCreationViewModel2(testBlueprint, packager)


		assert(toTest.pages.size == testGroups.size) { "Expected ${testGroups.size} pages but had ${toTest.pages.size}: ${toTest.pages}"}
		for (page in toTest.pages) {
			assert(page.type == CharacterCreationViewModel2.PageType.PROFICIENCY_SELECTION)
		}

	}

	@Test
	fun testWhenRunWithRobotThereAreThreePages() {
		val blueprint = Mockito.mock(DndCharacterBlueprint::class.java)
		val testClassOptionsRequirement = DndClassOptionsRequirement(CharacterCreationRobots.standardClassList)
		val testRaceOptionsRequirement = DndRaceOptionsRequirement(CharacterCreationRobots.standardRaceList)
		val testClassRequirement = DndClassRequirement(null, CharacterCreationRobots.standardClassList)
		val testRaceRequirement = DndRaceRequirement(null, CharacterCreationRobots.standardRaceList)
		val testProficiencyOptionsRequirement = DndProficiencyOptionsRequirement(CharacterCreationRobots.standardProficiencyGroupList)
		val testProficiencyRequirement = DndProficiencyRequirement(null, CharacterCreationRobots.standardProficiencyGroupList[0])

		val packager = Mockito.mock(CharacterCreationViewModel2.PageFactory::class.java)
		whenever(packager.pageFor(testClassOptionsRequirement)).thenReturn(CharacterCreationViewModel2.Page(CharacterCreationViewModel2.PageType.CLASS_SELECTION, "class_selection"))
		whenever(packager.pageFor(testClassRequirement)).thenReturn(CharacterCreationViewModel2.Page(CharacterCreationViewModel2.PageType.CLASS_SELECTION, "class_selection"))
		whenever(packager.pageFor(testRaceOptionsRequirement)).thenReturn(CharacterCreationViewModel2.Page(CharacterCreationViewModel2.PageType.RACE_SELECTION, "race_selection"))
		whenever(packager.pageFor(testRaceRequirement)).thenReturn(CharacterCreationViewModel2.Page(CharacterCreationViewModel2.PageType.RACE_SELECTION, "race_selection"))
		whenever(packager.pageFor(testProficiencyOptionsRequirement)).thenReturn(CharacterCreationViewModel2.Page(CharacterCreationViewModel2.PageType.PROFICIENCY_SELECTION, "proficiency_1"))
		whenever(packager.pageFor(testProficiencyRequirement)).thenReturn(CharacterCreationViewModel2.Page(CharacterCreationViewModel2.PageType.PROFICIENCY_SELECTION, "proficiency_1"))

		val testSubject = PublishSubject.create<List<Requirement<*>>>()
		whenever(blueprint.requirements).thenReturn(testSubject)
		val toTest = CharacterCreationViewModel2(blueprint, packager)
		val testObserver = TestObserver<CharacterCreationViewModel2.PageInsertion>()
		toTest.pageAdditions.subscribe(testObserver)

		testSubject.onNext(listOf(
				testClassOptionsRequirement,
				testClassRequirement,
				testRaceOptionsRequirement,
				testRaceRequirement,
				testProficiencyOptionsRequirement,
				testProficiencyRequirement))

		testObserver.await(30, TimeUnit.MILLISECONDS)
		testObserver.assertValueCount(3)

	}

}
