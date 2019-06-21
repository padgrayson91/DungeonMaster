package com.tendebit.dungeonmaster.charactercreation3.ability

import org.junit.Test

class TestDndAbilityDiceRoll {

	@Test
	fun testRollsInRange() {
		val toTest = DndAbilityDiceRoll()
		val rolls = ArrayList<Int>()
		for (i in 0..100) {
			rolls.add(toTest.roll())
		}

		assert(rolls.all { it in 3..18 })
	}

	@Test
	fun testRollDistribution() {
		val toTest = DndAbilityDiceRoll()
		val rolls = ArrayList<Int>()
		for (i in 0..100_000) {
			rolls.add(toTest.roll())
		}

		assert(rolls.average() > 9)
	}

}
