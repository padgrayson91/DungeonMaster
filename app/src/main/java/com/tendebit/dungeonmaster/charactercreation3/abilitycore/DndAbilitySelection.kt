package com.tendebit.dungeonmaster.charactercreation3.abilitycore

import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Locked
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Selection
import com.tendebit.dungeonmastercore.model.state.SelectionProvider
import io.reactivex.subjects.PublishSubject

class DndAbilitySelection(private val concurrency: Concurrency, initialState: Array<ItemState<out DndAbilitySlot>>,
						  initialRolls: DndAbilityRollSelection? = null) : SelectionProvider<Int> {

	override val internalStateChanges = PublishSubject.create<ItemState<out Selection<Int>>>()
	override val externalStateChanges = PublishSubject.create<ItemState<out Selection<Int>>>()
	val options = initialState
	private val rolls = initialRolls ?: DndAbilityRollSelection(options.size)
	override var selectionState = calculateRollState(rolls)
	val scoreOptions: List<ItemState<out Int>>
		get() = rolls.options

	fun autoRoll() {
		concurrency.runCalculation({ rolls.autoRollAll() })
	}

	fun manualRoll(index: Int, value: Int) {
		concurrency.runCalculation({ rolls.manualSet(index, value) })
	}

	override fun refresh() {
		selectionState = calculateRollState(rolls)
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

	private fun calculateRollState(rolls: DndAbilityRollSelection): ItemState<out DndAbilityRollSelection> {
		return if (rolls.options.all { it is Locked }) {
			Completed(rolls)
		} else {
			Normal(rolls)
		}
	}

	override fun toString(): String {
		return "User rolls: ${rolls.options} \n - Current Slots: $options"
	}

}
