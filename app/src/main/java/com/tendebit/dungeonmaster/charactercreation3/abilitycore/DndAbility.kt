package com.tendebit.dungeonmaster.charactercreation3.abilitycore

import java.io.Serializable

data class DndAbility(val bonus: DndAbilityBonus, val rawScore: Int) : Serializable {

	val type: DndAbilityType
		get() = bonus.type

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
