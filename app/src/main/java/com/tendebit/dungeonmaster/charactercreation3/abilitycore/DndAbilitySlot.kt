package com.tendebit.dungeonmaster.charactercreation3.abilitycore

import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Removed

class DndAbilitySlot(initialState: ItemState<out DndAbility>, private val bonus: DndAbilityBonus) {

	var state: ItemState<out DndAbility> = initialState
		private set
	private val type: DndAbilityType
		get() = bonus.type

	init {
		val typeForState = state.item?.type
		if (typeForState != null && typeForState != type) throw IllegalArgumentException("$state for ability slot has incorrect ability type; should be $type")
	}

	fun applyRoll(roll: Int) {
		val ability = DndAbility(bonus, roll)
		state = Completed(ability)
	}

	fun removeRoll() {
		if (state.item == null) throw IllegalStateException("No roll has been applied")
		state = Removed
	}

}
