package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel.DndAbilityDiceRollViewModel
import com.tendebit.dungeonmastercore.model.state.Locked
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Removed
import com.tendebit.dungeonmastercore.model.state.Selected
import io.reactivex.observers.TestObserver
import org.junit.Test

class TestDndAbilityDiceRollViewModel {

	@Test
	fun testForInitialRemovedStateModelIsInvisible() {
		val toTest = DndAbilityDiceRollViewModel(Removed)
		assert(!toTest.visibility)
	}

	@Test
	fun testModelBecomesInvisibleAfterStateChangedToRemoved() {
		val toTest = DndAbilityDiceRollViewModel(Selected(3))
		toTest.state = Removed
		assert(!toTest.visibility)
	}

	@Test
	fun testModelIsVisibleWithTextWithInitialNormalState() {
		val toTest = DndAbilityDiceRollViewModel(Normal(5))
		assert(toTest.visibility)
		assert(toTest.text == "5")
	}

	@Test
	fun testClickEmitsTrueWhenStateIsNormal() {
		val toTest = DndAbilityDiceRollViewModel(Normal(5))
		val testObserver = TestObserver<Boolean>()
		toTest.selection.subscribe(testObserver)
		toTest.onClick()
		testObserver.assertValue { it }
	}

	@Test
	fun testClickEmitsFalseWhenStateIsSelected() {
		val toTest = DndAbilityDiceRollViewModel(Selected(4))
		val testObserver = TestObserver<Boolean>()
		toTest.selection.subscribe(testObserver)
		toTest.onClick()
		testObserver.assertValue { !it }
	}

	@Test
	fun testClickDoesNotEmitWhenStateIsLocked() {
		val toTest = DndAbilityDiceRollViewModel(Locked(4))
		val testObserver = TestObserver<Boolean>()
		toTest.selection.subscribe(testObserver)
		toTest.onClick()
		assert(testObserver.valueCount() == 0)
	}

}