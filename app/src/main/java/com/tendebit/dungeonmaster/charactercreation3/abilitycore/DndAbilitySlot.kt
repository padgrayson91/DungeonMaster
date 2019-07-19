package com.tendebit.dungeonmaster.charactercreation3.abilitycore

import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Removed

class DndAbilitySlot(initialState: ItemState<out DndAbility>, val bonus: DndAbilityBonus) {

	var state: ItemState<out DndAbility> = initialState
		private set
	val modifier: Int?
		get() = state.item?.getModifier()
	val rawScore: Int?
		get() = state.item?.rawScore
	val type: DndAbilityType
		get() = bonus.type

	init {
		val typeForState = state.item?.type
		if (typeForState != null && typeForState != type) throw IllegalArgumentException("$state for ability slot has incorrect ability type; should be $type")
		val bonusForState = state.item?.bonus
		if (bonusForState != null && bonusForState != bonus) throw java.lang.IllegalArgumentException("Ability may not have a different bonus than the slot in which it resides")
	}

	fun applyRoll(roll: Int) {
		val ability = DndAbility(bonus, roll)
		state = Completed(ability)
	}

	fun removeRoll() {
		if (state.item == null) throw IllegalStateException("No roll has been applied")
		state = Removed
	}

	override fun toString(): String {
		return "Ability slot for: $state with bonus: $bonus"
	}
}
