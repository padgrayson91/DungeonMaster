package com.tendebit.dungeonmaster.charactercreation3.ability

import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbility
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityBonus
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityPrerequisites
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySlot
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySource
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityType
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.EMPTY_ABILITY_SLOTS
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.EMPTY_BONUS_ARRAY
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.logger
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.DelayedStart
import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.Disabled
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Loading
import com.tendebit.dungeonmastercore.model.state.Locked
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Removed
import com.tendebit.dungeonmastercore.model.state.Selected
import com.tendebit.dungeonmastercore.model.state.Undefined
import com.tendebit.dungeonmastercore.model.state.Waiting
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

class DndAbilities : DelayedStart<DndAbilityPrerequisites> {

	var state: ItemState<out DndAbilitySelection> = Loading
		private set
	private var disposable: Disposable? = null
	private lateinit var concurrency: Concurrency

	override fun start(prerequisites: DndAbilityPrerequisites) {
		state = Normal(DndAbilitySelection(prerequisites.concurrency, EMPTY_ABILITY_SLOTS))
		concurrency = prerequisites.concurrency
		disposable = Observable.combineLatest(prerequisites.sources) {
			sourceStates ->
			@Suppress("UNCHECKED_CAST")
			mergeBonuses((sourceStates.map { it as ItemState<out DndAbilitySource> }))
		}.subscribe {
			val oldState = state
			state = getStateForBonuses(it, oldState)
			logger.writeDebug("Updated state is: $state")
		}
	}

	fun stop() {
		disposable?.dispose()
	}

	private fun mergeBonuses(toMerge: List<ItemState<out DndAbilitySource>>): ItemState<out Array<DndAbilityBonus>> {
		return when {
			toMerge.any { it.item == null } -> Loading
			toMerge.isEmpty() -> Removed
			else -> Normal(mergeBonuses(toMerge.mapNotNull { it.item?.dndAbilityBonuses }))
		}
	}

	private fun mergeBonuses(toMerge: List<Array<DndAbilityBonus>>): Array<DndAbilityBonus> {
		if (toMerge.isEmpty()) return emptyArray()

		if (!toMerge.all { it.size == toMerge[0].size}) {
			throw IllegalArgumentException("Ability bonus lists from all sources must have the same size")
		}

		val result = Array(toMerge[0].size) { DndAbilityBonus(DndAbilityType.STR, 0) }
		for (i in 0 until toMerge[0].size) {
			var mergedBonus = 0
			val mergedType = toMerge[0][i].type
			for (abilityList in toMerge) {
				val abilityBonus = abilityList[i]
				if (abilityBonus.type != mergedType) throw IllegalArgumentException("Ability bonus lists must contain types in the same order")
				mergedBonus += abilityBonus.value
			}
			result[i] = DndAbilityBonus(mergedType, mergedBonus)
		}

		return result
	}

	private fun getStateForBonuses(bonuses: ItemState<out Array<DndAbilityBonus>>, oldState: ItemState<out DndAbilitySelection>): ItemState<out DndAbilitySelection> {
		val updatedSlotStates = getNewSlots(oldState.item?.options ?: EMPTY_ABILITY_SLOTS, bonuses.item ?: EMPTY_BONUS_ARRAY)
		return when (bonuses) {
			is Loading, Undefined -> Waiting(DndAbilitySelection(concurrency, updatedSlotStates))
			is Removed -> Removed
			else -> {
				if (bonuses.item?.isEmpty() != false) {
					Removed
				} else {
					Normal(DndAbilitySelection(concurrency, updatedSlotStates))
				}
			}
		}
	}

	private fun getNewSlots(slotStates: Array<ItemState<out DndAbilitySlot>>, bonuses: Array<DndAbilityBonus>): Array<ItemState<out DndAbilitySlot>> {
		return Array(slotStates.size) { applyBonusToSlot(slotStates[it], bonuses[it])}
	}

	private fun applyBonusToSlot(slotState: ItemState<out DndAbilitySlot>, bonus: DndAbilityBonus): ItemState<out DndAbilitySlot> {
		return when(slotState) {
			Loading, Undefined, Removed -> slotState
			is Waiting -> Waiting(DndAbilitySlot(applyBonusToAbility(bonus, slotState.item.state), bonus))
			is Selected -> Selected(DndAbilitySlot(applyBonusToAbility(bonus, slotState.item.state), bonus))
			is Disabled -> Disabled(DndAbilitySlot(applyBonusToAbility(bonus, slotState.item.state), bonus))
			is Locked -> Locked(DndAbilitySlot(applyBonusToAbility(bonus, slotState.item.state), bonus))
			is Normal -> Normal(DndAbilitySlot(applyBonusToAbility(bonus, slotState.item.state), bonus))
			is Completed -> Completed(DndAbilitySlot(applyBonusToAbility(bonus, slotState.item.state), bonus))
		}
	}

	private fun applyBonusToAbility(bonus: DndAbilityBonus, abilityState: ItemState<out DndAbility>): ItemState<out DndAbility> {
		return when(abilityState) {
			Loading, Undefined, Removed -> abilityState
			is Waiting -> Waiting(DndAbility(bonus, abilityState.item.rawScore))
			is Selected -> Selected(DndAbility(bonus, abilityState.item.rawScore))
			is Disabled -> Disabled(DndAbility(bonus, abilityState.item.rawScore))
			is Locked -> Locked(DndAbility(bonus, abilityState.item.rawScore))
			is Normal -> Normal(DndAbility(bonus, abilityState.item.rawScore))
			is Completed -> Completed(DndAbility(bonus, abilityState.item.rawScore))
		}
	}

}
