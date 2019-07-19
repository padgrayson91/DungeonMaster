package com.tendebit.dungeonmaster.charactercreation3.abilitycore

import com.tendebit.dungeonmastercore.model.dice.DiceRoll
import com.tendebit.dungeonmastercore.model.dice.MultipleDiceRoll
import com.tendebit.dungeonmastercore.model.dice.SingleDiceRoll

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
