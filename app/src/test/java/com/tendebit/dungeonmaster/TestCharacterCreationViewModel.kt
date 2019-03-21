package com.tendebit.dungeonmaster


import com.tendebit.dungeonmaster.charactercreation2.CharacterCreationViewModel2
import com.tendebit.dungeonmaster.charactercreation2.feature.DndCharacter
import com.tendebit.dungeonmaster.charactercreation2.pager.Page
import com.tendebit.dungeonmaster.core.blueprint.Blueprint
import com.tendebit.dungeonmaster.core.blueprint.Delta
import io.reactivex.observers.TestObserver
import org.junit.Test
import org.mockito.Mockito.`when` as whenever

@Suppress("UNCHECKED_CAST")
class TestCharacterCreationViewModel {

	@Test
	fun testInitialStateHasNoPages() {
		val testBlueprint = Blueprint(emptyList(), DndCharacter())
		val toTest = CharacterCreationViewModel2(testBlueprint)
		val testObserver = TestObserver<List<Delta<Page>>>()

		toTest.pageChanges.subscribe(testObserver)

		testObserver.assertEmpty()
		assert(toTest.pages.isEmpty())
	}

	@Test
	fun testInitialStateIsLoading() {
		val testBlueprint = Blueprint(emptyList(), DndCharacter())
		val toTest = CharacterCreationViewModel2(testBlueprint)

		assert(toTest.isLoading)
	}

}
