package com.tendebit.dungeonmaster.charactercreation3.ability

import java.io.Serializable

data class DndAbility(val type: Type, val rawScore: Int) : Serializable {

	enum class Type {
		STR,
		DEX,
		CON,
		INT,
		WIS,
		CHA
	}

	fun getModifier(): Int {
		val isEven = rawScore and 0x1 == 0
		return if (isEven) {
			(rawScore - 10)/2
		} else {
			(rawScore - 11)/2
		}
	}

}
