package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmaster.core.model.DiceRoll
import com.tendebit.dungeonmaster.core.model.MultipleDiceRoll
import com.tendebit.dungeonmaster.core.model.SingleDiceRoll

class DndAbilityDiceRoll : DiceRoll {

	private val multiRoll = MultipleDiceRoll(listOf(
			SingleDiceRoll(6),
			SingleDiceRoll(6),
			SingleDiceRoll(6),
			SingleDiceRoll(6)))

	override fun roll(): Int {
		return multiRoll.rollChooseHighest(3)
	}

}
