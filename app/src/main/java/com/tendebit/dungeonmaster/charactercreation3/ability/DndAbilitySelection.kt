package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import com.tendebit.dungeonmaster.core.model.ItemState
import com.tendebit.dungeonmaster.core.model.Removed
import com.tendebit.dungeonmaster.core.model.Selected

class DndAbilitySelection(private val concurrency: Concurrency, initialState: Array<ItemState<out DndAbility>>? = null, initialRolls: DndAbilityRollSelection? = null) {

	private val abilitySlots = DndAbility.Type.values()
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
			options[index] = Selected(DndAbility(abilitySlots[index], item.item))
			rolls.onAssigned()
		})
	}

}
