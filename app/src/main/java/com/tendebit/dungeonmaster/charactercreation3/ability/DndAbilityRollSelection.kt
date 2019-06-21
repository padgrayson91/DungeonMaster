package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmaster.core.model.BaseSelection
import com.tendebit.dungeonmaster.core.model.ItemState
import com.tendebit.dungeonmaster.core.model.ListItemState
import com.tendebit.dungeonmaster.core.model.Locked
import com.tendebit.dungeonmaster.core.model.Normal
import com.tendebit.dungeonmaster.core.model.Removed

class DndAbilityRollSelection(abilityCount: Int, initialState: List<ItemState<out Int>>? = null) : BaseSelection<Int>() {

	override val options = ArrayList(initialState ?: Array<ItemState<out Int>>(abilityCount) { Removed }.toList())
	private val roll = DndAbilityDiceRoll()

	private fun autoRoll(position: Int) {
		options[position] = Normal(roll.roll())
	}

	fun autoRollAll() {
		for (i in 0 until options.size) {
			autoRoll(i)
		}
	}

	fun manualSet(position: Int, value: Int) {
		if (options[position] is Locked) throw IllegalArgumentException("Cannot manually set value for locked item at $position")
		val normalState = Normal(value)
		options[position] = normalState
		directSelectionChanges.onNext(ListItemState(position, normalState))
	}

	fun onAssigned() {
		val oldState = selectedItem ?: throw IllegalArgumentException("Cannot assign without first making a selection")
		val index = selectedIndex
		val lockedState = Locked(oldState.item)
		options[index] = lockedState
		indirectSelectionChanges.onNext(ListItemState(index, lockedState))
	}

	fun onRetracted(oldState: Locked<Int>) {
		val position = options.indexOf(oldState)
		if (position == -1) throw IllegalArgumentException("$oldState was not present")
		val normalState = Normal(oldState.item)
		options[position] = normalState
		indirectSelectionChanges.onNext(ListItemState(position, normalState))
	}

}
