package com.tendebit.dungeonmastercore

import com.tendebit.dungeonmastercore.model.dice.DiceRoll
import com.tendebit.dungeonmastercore.model.dice.MultipleDiceRoll
import org.junit.Test
import org.mockito.Mockito
import org.mockito.Mockito.`when` as whenever

class TestMultipleDiceRoll {

	@Test
	fun testValidSum() {
		val mockRollSequence = Mockito.mock(DiceRoll::class.java)
		whenever(mockRollSequence.roll()).thenReturn(3)
				.thenReturn(4)
				.thenReturn(5)
				.thenReturn(6)
				.thenReturn(7)
				.thenReturn(8)

		val rollList = listOf(
				mockRollSequence,
				mockRollSequence,
				mockRollSequence,
				mockRollSequence,
				mockRollSequence,
				mockRollSequence)

		val toTest = MultipleDiceRoll(rollList)
		assert(toTest.roll() == 33)
	}

	@Test
	fun testChooseHighest() {
		val mockRollSequence = Mockito.mock(DiceRoll::class.java)
		whenever(mockRollSequence.roll()).thenReturn(3)
				.thenReturn(4)
				.thenReturn(5)
				.thenReturn(6)
				.thenReturn(7)
				.thenReturn(8)

		val rollList = listOf(
				mockRollSequence,
				mockRollSequence,
				mockRollSequence,
				mockRollSequence,
				mockRollSequence,
				mockRollSequence)

		val toTest = MultipleDiceRoll(rollList)
		assert(toTest.rollChooseHighest(3) == 21)
	}

	@Test
	fun testChooseLowest() {
		val mockRollSequence = Mockito.mock(DiceRoll::class.java)
		whenever(mockRollSequence.roll()).thenReturn(3)
				.thenReturn(4)
				.thenReturn(5)
				.thenReturn(6)
				.thenReturn(7)
				.thenReturn(8)

		val rollList = listOf(
				mockRollSequence,
				mockRollSequence,
				mockRollSequence,
				mockRollSequence,
				mockRollSequence,
				mockRollSequence)

		val toTest = MultipleDiceRoll(rollList)
		assert(toTest.rollChooseLowest(5) == 25)
	}

}