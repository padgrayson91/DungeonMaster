package com.tendebit.dungeonmaster.charactercreation3.ability

import org.junit.Test

class TestDndAbility {

	@Test
	fun testPositiveAbilityScore() {
		val toTest = DndAbility(DndAbilityType.STR, 14, 0)
		assert(toTest.getModifier() == 2)
	}

	@Test
	fun testNegativeAbilityScore() {
		val toTest = DndAbility(DndAbilityType.STR, 7, 0)
		assert(toTest.getModifier() == -2)
	}

	@Test
	fun testNeutralAbilityScore() {
		val toTest = DndAbility(DndAbilityType.STR, 11, 0)
		assert(toTest.getModifier() == 0)
	}

	@Test
	fun testPositiveAbilityScore2() {
		val toTest = DndAbility(DndAbilityType.STR, 17, 0)
		assert(toTest.getModifier() == 3)
	}

	@Test
	fun testNegativeAbilityScore2() {
		val toTest = DndAbility(DndAbilityType.STR, 4, 0)
		assert(toTest.getModifier() == -3)
	}

	@Test
	fun testNeutralAbilityScore2() {
		val toTest = DndAbility(DndAbilityType.STR, 10, 0)
		assert(toTest.getModifier() == 0)
	}

	@Test
	fun testPositiveAbilityScore3() {
		val toTest = DndAbility(DndAbilityType.STR, 30, 0)
		assert(toTest.getModifier() == 10)
	}

	@Test
	fun testNegativeAbilityScore3() {
		val toTest = DndAbility(DndAbilityType.STR, 1, 0)
		assert(toTest.getModifier() == -5)
	}

}
