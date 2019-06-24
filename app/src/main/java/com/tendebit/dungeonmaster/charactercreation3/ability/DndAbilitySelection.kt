package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Removed
import com.tendebit.dungeonmastercore.model.state.Selected

class DndAbilitySelection(private val concurrency: Concurrency, private val bonuses: List<DndAbilityBonus> = emptyList(),
						  initialState: Array<ItemState<out DndAbility>>? = null, initialRolls: DndAbilityRollSelection? = null) {

	private val abilitySlots = DndAbilityType.values()
	val options = initialState ?: Array<ItemState<out DndAbility>>(abilitySlots.size) { Removed }
	private val rolls = initialRolls ?: DndAbilityRollSelection(abilitySlots.size)
	val scoreOptions: List<ItemState<out Int>>
		get() = rolls.options

	fun autoRoll() {
		concurrency.runCalculation({ rolls.autoRollAll() })
	}

	fun manualRoll(index: Int, value: Int) {
		concurrency.runCalculation({ rolls.manualSet(index, value) })
	}

	fun performScoreSelection(index: Int) {
		concurrency.runCalculation({
			rolls.select(index)
		})
	}

	fun performAssignment(index: Int) {
		concurrency.runCalculation({
			val item = rolls.selectedItem ?: throw IllegalStateException("Cannot perform assignment without making a selection")
			val abilityType = abilitySlots[index]
			val bonus = bonuses.find { it.type == abilityType }
			options[index] = Selected(DndAbility(abilitySlots[index], item.item, bonus ?: DndAbilityBonus(abilityType)))
			rolls.onAssigned()
		})
	}

}
