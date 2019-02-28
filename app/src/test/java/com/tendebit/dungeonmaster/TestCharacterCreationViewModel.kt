package com.tendebit.dungeonmaster

import com.tendebit.dungeonmaster.charactercreation.feature.DndCharacterBlueprint
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceRequirement
import com.tendebit.dungeonmaster.charactercreation.viewpager.BasePageCollection
import com.tendebit.dungeonmaster.charactercreation.viewpager.CharacterCreationViewModel2
import com.tendebit.dungeonmaster.charactercreation.viewpager.PageInsertion
import com.tendebit.dungeonmaster.charactercreation.viewpager.PageRemoval
import com.tendebit.dungeonmaster.charactercreation.viewpager.ViewModel
import com.tendebit.dungeonmaster.charactercreation.viewpager.ViewModelFactory
import io.reactivex.Observable
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

@Suppress("UNCHECKED_CAST")
class TestCharacterCreationViewModel {

	@Test
	fun testInitialStateHasNoPages() {
		val testBlueprint = DndCharacterBlueprint()
		val packager = Mockito.mock(ViewModelFactory::class.java)
		val toTest = CharacterCreationViewModel2(testBlueprint, packager, BasePageCollection())
		val testObserver = TestObserver<PageInsertion>()

		toTest.pageAdditions.subscribe(testObserver)

		testObserver.assertEmpty()
		assert(toTest.pages.isEmpty())
	}

	@Test
	fun testInitialStateIsLoading() {
		val testBlueprint = DndCharacterBlueprint()
		val packager = Mockito.mock(ViewModelFactory::class.java)
		val toTest = CharacterCreationViewModel2(testBlueprint, packager, BasePageCollection())

		assert(toTest.isLoading)
	}

	@Test
	fun testInitialStateHasNoPageRemovals() {
		val testBlueprint = DndCharacterBlueprint()
		val packager = Mockito.mock(ViewModelFactory::class.java)
		val toTest = CharacterCreationViewModel2(testBlueprint, packager, BasePageCollection())
		val testObserver = TestObserver<PageRemoval>()

		toTest.pageRemovals.subscribe(testObserver)

		testObserver.assertEmpty()
	}

	@Test
	fun testHasOnePageWhenPackagerEmitsOnePage() {
		val testBlueprint = Mockito.mock(DndCharacterBlueprint::class.java)
		val packager = Mockito.mock(ViewModelFactory::class.java)
		val testRequirement = DndClassRequirement(null, emptyList())
		val mockViewModel = Mockito.mock(ViewModel::class.java)
		whenever(packager.viewModelFor(testRequirement)).thenReturn(mockViewModel)
		whenever(testBlueprint.requirements).thenReturn(Observable.fromArray(listOf(testRequirement)))
		val toTest = CharacterCreationViewModel2(testBlueprint, packager, BasePageCollection())


		assert(toTest.pages.size == 1)
		assert(toTest.pages[0] == mockViewModel)
	}

	@Test
	fun testHasTwoPagesWhenPackagerEmitsTwoPages() {
		val testBlueprint = Mockito.mock(DndCharacterBlueprint::class.java)
		val packager = Mockito.mock(ViewModelFactory::class.java)
		val testRequirement = DndClassRequirement(null, emptyList())
		val otherRequirement = DndRaceRequirement(null, emptyList())
		val mockViewModel1 = Mockito.mock(ViewModel::class.java)
		val mockViewModel2 = Mockito.mock(ViewModel::class.java)
		whenever(testBlueprint.requirements).thenReturn(Observable.fromArray(listOf(
				testRequirement,
				otherRequirement)))
		whenever(packager.viewModelFor(testRequirement)).thenReturn(mockViewModel1)
		whenever(packager.viewModelFor(otherRequirement)).thenReturn(mockViewModel2)


		val toTest = CharacterCreationViewModel2(testBlueprint, packager, BasePageCollection())


		assert(toTest.pages.size == 2)
		assert(toTest.pages[0] == mockViewModel1)
		assert(toTest.pages[1] == mockViewModel2)
	}

}
