package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmaster.testhelpers.CharacterCreationRobots
import com.tendebit.dungeonmaster.testhelpers.TestConcurrency
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Removed
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Test

@ExperimentalCoroutinesApi
class TestDndAbilitySelection {

	private val concurrency = TestConcurrency

	@Test
	fun testPerformRoll() {
		val toTest = DndAbilitySelection(concurrency, Array(6) { Removed })
		toTest.autoRoll()
		assert(toTest.scoreOptions.all { it is Normal })
	}

	@Test
	fun testPerformSingleSelectionAndAssignment() {
		val toTest = DndAbilitySelection(concurrency, CharacterCreationRobots.emptyAbilitySlotStateList)
		toTest.autoRoll()

		toTest.performScoreSelection(3)
		toTest.performAssignment(5)

		assert(toTest.options[5].item?.state?.item?.rawScore == toTest.scoreOptions[3].item ?: Int.MIN_VALUE)
	}

	@Test
	fun testPerformFullSelection() {
		val toTest = DndAbilitySelection(concurrency, CharacterCreationRobots.emptyAbilitySlotStateList)
		toTest.autoRoll()

		toTest.performScoreSelection(3)
		toTest.performAssignment(5)
		toTest.performScoreSelection(0)
		toTest.performAssignment(4)
		toTest.performScoreSelection(1)
		toTest.performAssignment(2)
		toTest.performScoreSelection(4)
		toTest.performAssignment(3)
		toTest.performScoreSelection(5)
		toTest.performAssignment(0)
		toTest.performScoreSelection(2)
		toTest.performAssignment(1)

		assert(toTest.options[5].item?.state?.item?.rawScore == toTest.scoreOptions[3].item ?: Int.MIN_VALUE)
		assert(toTest.options[4].item?.state?.item?.rawScore == toTest.scoreOptions[0].item ?: Int.MIN_VALUE)
		assert(toTest.options[2].item?.state?.item?.rawScore == toTest.scoreOptions[1].item ?: Int.MIN_VALUE)
		assert(toTest.options[3].item?.state?.item?.rawScore == toTest.scoreOptions[4].item ?: Int.MIN_VALUE)
		assert(toTest.options[0].item?.state?.item?.rawScore == toTest.scoreOptions[5].item ?: Int.MIN_VALUE)
		assert(toTest.options[1].item?.state?.item?.rawScore == toTest.scoreOptions[2].item ?: Int.MIN_VALUE)
	}

}
