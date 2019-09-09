package com.tendebit.dungeonmaster.charactercreation3.ability

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.AbilityProvider
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbility
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityBonus
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityPrerequisites
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySelection
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
import com.tendebit.dungeonmastercore.model.state.ItemStateUtils
import com.tendebit.dungeonmastercore.model.state.Loading
import com.tendebit.dungeonmastercore.model.state.Locked
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Removed
import com.tendebit.dungeonmastercore.model.state.Selected
import com.tendebit.dungeonmastercore.model.state.Undefined
import com.tendebit.dungeonmastercore.model.state.Waiting
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class DndAbilities : DelayedStart<DndAbilityPrerequisites>, AbilityProvider, Parcelable {

	override var state: ItemState<out DndAbilitySelection> = Loading
		private set
	override val internalStateChanges = BehaviorSubject.create<ItemState<out DndAbilitySelection>>()
	override val externalStateChanges = BehaviorSubject.create<ItemState<out DndAbilitySelection>>()
	private var disposable: Disposable? = null

	constructor()

	private constructor(parcel: Parcel) {
		state = ItemStateUtils.readItemStateFromParcel(parcel)
		logger.writeDebug("Got $state from parcel")
	}

	override fun start(prerequisites: DndAbilityPrerequisites) {
		state = Normal(DndAbilitySelection(prerequisites.concurrency, EMPTY_ABILITY_SLOTS))
		externalStateChanges.onNext(state)
		prerequisites.concurrency.runCalculation({
			disposable = Observable.combineLatest(prerequisites.sources) {
				sourceStates ->
				@Suppress("UNCHECKED_CAST")
				mergeBonuses((sourceStates.map { it as ItemState<out DndAbilitySource> }))
			}.subscribe {
				val oldState = state
				state = getStateForBonuses(it, oldState, prerequisites.concurrency)
				externalStateChanges.onNext(state)
				logger.writeDebug("Updated state is: $state")
			}
		})
	}

	fun stop() {
		disposable?.dispose()
	}

	override fun refreshAbilityState() {
		val oldState = state
		logger.writeDebug("Performing state check. Current state is $oldState")
		val newState = when (oldState) {
			is Normal -> {
				if (oldState.item.options.all { it is Completed }) {
					Completed(oldState.item)
				} else {
					oldState
				}
			}
			is Completed -> {
				if (oldState.item.options.all { it is Completed }) {
					oldState
				} else {
					Normal(oldState.item)
				}
			}
			else -> oldState
		}
		logger.writeDebug("State has been updated to $newState")

		if (oldState != newState) {
			state = newState
			internalStateChanges.onNext(state)
		}
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

	private fun getStateForBonuses(bonuses: ItemState<out Array<DndAbilityBonus>>, oldState: ItemState<out DndAbilitySelection>, concurrency: Concurrency): ItemState<out DndAbilitySelection> {
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

	override fun writeToParcel(dest: Parcel?, flags: Int) {
		com.tendebit.dungeonmaster.charactercreation3.characterclass.logger.writeDebug("Parcelizing $this")
		dest?.let {
			ItemStateUtils.writeItemStateToParcel(state, it)
		}
	}

	override fun describeContents(): Int = 0

	companion object CREATOR : Parcelable.Creator<DndAbilities> {

		override fun createFromParcel(source: Parcel): DndAbilities {
			return DndAbilities(source)
		}

		override fun newArray(size: Int): Array<DndAbilities?> {
			return arrayOfNulls(size)
		}
	}

}
