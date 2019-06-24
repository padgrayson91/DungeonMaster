package com.tendebit.dungeonmaster.charactercreation3.ability

import java.io.Serializable

data class DndAbility(val type: DndAbilityType, val rawScore: Int, val bonus: DndAbilityBonus = DndAbilityBonus(type)) : Serializable {

	fun getModifier(): Int {
		val scoreWithBonus = rawScore + bonus.value
		val isEven = scoreWithBonus and 0x1 == 0
		return if (isEven) {
			(scoreWithBonus - 10)/2
		} else {
			(scoreWithBonus - 11)/2
		}
	}

}
