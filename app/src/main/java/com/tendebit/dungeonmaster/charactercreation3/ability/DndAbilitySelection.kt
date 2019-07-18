package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySlot
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.ItemState

class DndAbilitySelection(private val concurrency: Concurrency, initialState: Array<ItemState<out DndAbilitySlot>>,
						  initialRolls: DndAbilityRollSelection? = null) {

	val options = initialState
	private val rolls = initialRolls ?: DndAbilityRollSelection(options.size)
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
			val rollState = rolls.selectedItem ?: throw IllegalStateException("Cannot perform assignment without making a selection")
			val oldSlot = options[index].item ?: throw IllegalArgumentException("Unable to apply roll to slot at index $index")
			oldSlot.applyRoll(rollState.item)
			options[index] = Completed(oldSlot)
			rolls.onAssigned()
		})
	}

}
