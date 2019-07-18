package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbility
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityBonus
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityType
import org.junit.Test

class TestDndAbility {

	@Test
	fun testPositiveAbilityScore() {
		val bonus = DndAbilityBonus(DndAbilityType.STR, 0)
		val toTest = DndAbility(bonus, 14)
		assert(toTest.getModifier() == 2)
	}

	@Test
	fun testNegativeAbilityScore() {
		val bonus = DndAbilityBonus(DndAbilityType.STR, 0)
		val toTest = DndAbility(bonus, 7)
		assert(toTest.getModifier() == -2)
	}

	@Test
	fun testNeutralAbilityScore() {
		val bonus = DndAbilityBonus(DndAbilityType.STR, 0)
		val toTest = DndAbility(bonus, 11)
		assert(toTest.getModifier() == 0)
	}

	@Test
	fun testPositiveAbilityScore2() {
		val bonus = DndAbilityBonus(DndAbilityType.STR, 0)
		val toTest = DndAbility(bonus, 17)
		assert(toTest.getModifier() == 3)
	}

	@Test
	fun testNegativeAbilityScore2() {
		val bonus = DndAbilityBonus(DndAbilityType.STR, 0)
		val toTest = DndAbility(bonus, 4)
		assert(toTest.getModifier() == -3)
	}

	@Test
	fun testNeutralAbilityScore2() {
		val bonus = DndAbilityBonus(DndAbilityType.STR, 0)
		val toTest = DndAbility(bonus, 10)
		assert(toTest.getModifier() == 0)
	}

	@Test
	fun testPositiveAbilityScore3() {
		val bonus = DndAbilityBonus(DndAbilityType.STR, 0)
		val toTest = DndAbility(bonus, 30)
		assert(toTest.getModifier() == 10)
	}

	@Test
	fun testNegativeAbilityScore3() {
		val bonus = DndAbilityBonus(DndAbilityType.STR, 0)
		val toTest = DndAbility(bonus, 1)
		assert(toTest.getModifier() == -5)
	}

}
