package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Removed

class DndAbilitySlot(initialState: ItemState<out DndAbility>, val type: DndAbilityType, private val bonus: Int) {

	var state: ItemState<out DndAbility> = initialState
		private set

	init {
		val typeForState = state.item?.type
		if (typeForState != null && typeForState != type) throw IllegalArgumentException("$state for ability slot has incorrect ability type; should be $type")
	}

	fun applyRoll(roll: Int) {
		val ability = DndAbility(type, roll, bonus)
		state = Completed(ability)
	}

	fun removeRoll() {
		if (state.item == null) throw IllegalStateException("No roll has been applied")
		state = Removed
	}

}
