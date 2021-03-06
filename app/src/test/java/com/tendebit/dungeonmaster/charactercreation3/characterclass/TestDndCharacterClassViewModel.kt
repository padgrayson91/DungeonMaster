package com.tendebit.dungeonmaster.charactercreation3.characterclass

import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassViewModel
import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Selected
import com.tendebit.dungeonmastercore.viewmodel3.TextTypes
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestDndCharacterClassViewModel {

	@Test
	fun testNormalItemUsesNormalTextType() {
		val testItem = Normal(CharacterCreationRobots.standardClassListV2[0])
		val toTest = DndCharacterClassViewModel(testItem)

		assert(toTest.textType == TextTypes.NORMAL)
	}

	@Test
	fun testSelectedItemUsesSelectedTextType() {
		val testItem = Selected(CharacterCreationRobots.standardClassListV2[0])
		val toTest = DndCharacterClassViewModel(testItem)

		assert(toTest.textType == TextTypes.SELECTED)
	}


	@Test
	fun testSelectionEmitsOnClick() {
		val testItem = Normal(CharacterCreationRobots.standardClassListV2[0])
		val toTest = DndCharacterClassViewModel(testItem)
		val testObserver = TestObserver<Boolean>()
		toTest.selection.subscribe(testObserver)

		toTest.onClick()

		assert(testObserver.valueCount() == 1)
		testObserver.assertValue { it }
	}

	@Test
	fun testDeselectionEmitsOnSelectedClick() {
		val testItem = Selected(CharacterCreationRobots.standardClassListV2[0])
		val toTest = DndCharacterClassViewModel(testItem)
		val testObserver = TestObserver<Boolean>()
		toTest.selection.subscribe(testObserver)

		toTest.onClick()

		assert(testObserver.valueCount() == 1)
		testObserver.assertValue { !it }
	}

}