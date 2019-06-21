package com.tendebit.dungeonmaster.core

import com.tendebit.dungeonmaster.core.model.SingleDiceRoll
import org.junit.Test

class TestSingleDiceRoll {

	@Test
	fun testD20DiceRollInBounds() {
		val upperBound = 20
		val toTest = SingleDiceRoll(upperBound)
		val rolls = ArrayList<Int>()
		for (i in 0..100) {
			rolls.add(toTest.roll())
		}

		assert(rolls.all { it <= upperBound })
		assert(rolls.all { it > 0 })
		assert(!rolls.all { it == rolls[0] })
	}

	@Test
	fun testD12DiceRollInBounds() {
		val upperBound = 12
		val toTest = SingleDiceRoll(upperBound)
		val rolls = ArrayList<Int>()
		for (i in 0..100) {
			rolls.add(toTest.roll())
		}

		assert(rolls.all { it <= upperBound })
		assert(rolls.all { it > 0 })
		assert(!rolls.all { it == rolls[0] })
	}

	@Test
	fun testD10DiceRollInBounds() {
		val upperBound = 10
		val toTest = SingleDiceRoll(upperBound)
		val rolls = ArrayList<Int>()
		for (i in 0..100) {
			rolls.add(toTest.roll())
		}

		assert(rolls.all { it <= upperBound })
		assert(rolls.all { it > 0 })
		assert(!rolls.all { it == rolls[0] })
	}

	@Test
	fun testD8DiceRollInBounds() {
		val upperBound = 8
		val toTest = SingleDiceRoll(upperBound)
		val rolls = ArrayList<Int>()
		for (i in 0..100) {
			rolls.add(toTest.roll())
		}

		assert(rolls.all { it <= upperBound })
		assert(rolls.all { it > 0 })
		assert(!rolls.all { it == rolls[0] })
	}

	@Test
	fun testD6DiceRollInBounds() {
		val upperBound = 6
		val toTest = SingleDiceRoll(upperBound)
		val rolls = ArrayList<Int>()
		for (i in 0..100) {
			rolls.add(toTest.roll())
		}

		assert(rolls.all { it <= upperBound })
		assert(rolls.all { it > 0 })
		assert(!rolls.all { it == rolls[0] })
	}

	@Test
	fun testD4DiceRollInBounds() {
		val upperBound = 4
		val toTest = SingleDiceRoll(upperBound)
		val rolls = ArrayList<Int>()
		for (i in 0..100) {
			rolls.add(toTest.roll())
		}

		assert(rolls.all { it <= upperBound })
		assert(rolls.all { it > 0 })
		assert(!rolls.all { it == rolls[0] })
	}

}
