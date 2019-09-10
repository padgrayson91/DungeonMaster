package com.tendebit.dungeonmaster.charactercreation3.proficiency

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiency
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiencySource
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.ProficiencyPrerequisites
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.ProficiencyProvider
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.logger
import com.tendebit.dungeonmastercore.model.DelayedStart
import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.ItemStateUtils
import com.tendebit.dungeonmastercore.model.state.Loading
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Removed
import com.tendebit.dungeonmastercore.model.state.Undefined
import com.tendebit.dungeonmastercore.model.state.Waiting
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

/**
 * Top-level model for dealing with character proficiencies. Maintains the state of the [DndProficiencySelection]
 * object which contains the user's currently selected and available proficiency options
 */
class DndProficiencies : ProficiencyProvider, Parcelable, DelayedStart<ProficiencyPrerequisites> {

	override var state: ItemState<out DndProficiencySelection> = Removed

	override val internalStateChanges = PublishSubject.create<ItemState<out DndProficiencySelection>>()
	override val externalStateChanges = PublishSubject.create<ItemState<out DndProficiencySelection>>()

	private var disposable: Disposable? = null

	constructor()

	constructor(parcel: Parcel) {
		state = ItemStateUtils.readItemStateFromParcel(parcel)
	}

	override fun start(prerequisites: ProficiencyPrerequisites) {
		prerequisites.concurrency.runCalculation({
			disposable = Observable.combineLatest(prerequisites.sources) {
				sourceStates ->
				logger.writeDebug("Had ${sourceStates.size} sources")
				sourceStates.forEach { logger.writeDebug("$it") }
				@Suppress("UNCHECKED_CAST")
				sourceStates.map { it as ItemState<out DndProficiencySource> }
			}.subscribe {
				updateStateForSourceChange(it)
			}
		})
	}

	fun stop() {
		disposable?.dispose()
	}

	override fun refreshProficiencyState() {
		val oldState = state
		logger.writeDebug("Performing state check. Current state is $oldState")
		val newState = doCalculateState(oldState)
		logger.writeDebug("State has been updated to $newState")

		if (oldState != newState) {
			state = newState
			internalStateChanges.onNext(state)
		}
	}

	private fun updateStateForSourceChange(sources: List<ItemState<out DndProficiencySource>>) {
		val oldState = state
		logger.writeDebug("A proficiency source has changed")

		if (sources.all { it.item == null }) {
			state = if (sources.any { it is Loading }) Undefined else Removed
			if (state == oldState) {
				return
			}
			logger.writeDebug("No proficiency sources available, new state is $state")
			externalStateChanges.onNext(state)
			return
		}

		if (sources.any { it is Loading }) {
			val oldItem = oldState.item
			state = if (oldItem != null) Waiting(oldItem) else Undefined
			if (state == oldState) {
				return
			}
			logger.writeDebug("Proficiency source is loading, new state is $state")
			externalStateChanges.onNext(state)
			return
		}

		logger.writeDebug("Updating proficiencies from all sources")
		val proficiencyOptions = sources.mapNotNull { it.item }.map { it.dndProficiencyOptions }.flatten()
		val oldOptions = oldState.item?.groupStates?.mapNotNull { it.item } ?: emptyList()
		val deselectedItems = ArrayList<DndProficiency>()
		for (group in oldOptions) {
			val updatedGroup = proficiencyOptions.find { it.contentsMatch(group) }
			if (updatedGroup == null) {
				// Group was removed; we'll need to notify other groups that its selections are deselected
				logger.writeDebug("Proficiency group $group was no longer present")
				deselectedItems.addAll(group.selections.mapNotNull { it.item })
			} else {
				// Group is present in updated options, update new state to match old state
				updatedGroup.copy(group)
			}
		}

		for (item in deselectedItems) {
			for (group in proficiencyOptions) {
				group.onExternalDeselection(item)
			}
		}

		state = doCalculateState(oldState, DndProficiencySelection(proficiencyOptions))
		logger.writeDebug("Got $state")
		if (oldState != state) {
			externalStateChanges.onNext(state)
		}
	}

	private fun doCalculateState(oldState: ItemState<out DndProficiencySelection>, newSelection: DndProficiencySelection? = null): ItemState<out DndProficiencySelection> {
		return when (oldState) {
			is Normal -> {
				val targetSelection = newSelection ?: oldState.item
				if (targetSelection.groupStates.all { it is Completed }) {
					Completed(targetSelection)
				} else {
					oldState
				}
			}
			is Completed -> {
				val targetSelection = newSelection ?: oldState.item
				if (targetSelection.groupStates.all { it is Completed }) {
					oldState
				} else {
					Normal(targetSelection)
				}
			}
			is Undefined -> {
				if (newSelection == null) {
					oldState
				} else {
					Normal(newSelection)
				}
			}
			else -> oldState
		}
	}

	override fun writeToParcel(dest: Parcel?, flags: Int) {
		dest?.let {
			ItemStateUtils.writeItemStateToParcel(state, it)
		}
	}

	override fun describeContents(): Int = 0

	companion object CREATOR : Parcelable.Creator<DndProficiencies> {

		override fun createFromParcel(source: Parcel): DndProficiencies {
			return DndProficiencies(source)
		}

		override fun newArray(size: Int): Array<DndProficiencies?> {
			return arrayOfNulls(size)
		}
	}

}
