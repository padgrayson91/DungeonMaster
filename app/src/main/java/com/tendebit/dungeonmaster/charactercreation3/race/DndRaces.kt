package com.tendebit.dungeonmaster.charactercreation3.race

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySource
import com.tendebit.dungeonmaster.charactercreation3.race.data.DndRaceDataStore
import com.tendebit.dungeonmaster.charactercreation3.race.data.DndRacePrerequisites
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.DelayedStart
import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.ItemStateUtils
import com.tendebit.dungeonmastercore.model.state.Loading
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Removed
import com.tendebit.dungeonmastercore.model.state.Selected
import com.tendebit.dungeonmastercore.model.state.Selection
import com.tendebit.dungeonmastercore.model.state.SelectionProvider
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DndRaces : SelectionProvider<DndRace>, DelayedStart<DndRacePrerequisites>, Parcelable {

	override var selectionState: ItemState<out Selection<DndRace>> = Loading

	override val internalStateChanges = PublishSubject.create<ItemState<out Selection<DndRace>>>()
	override val externalStateChanges = PublishSubject.create<ItemState<out Selection<DndRace>>>()
	private val internalSelectedRaceDetails = PublishSubject.create<ItemState<out DndDetailedRace>>()
	@Suppress("UNCHECKED_CAST")
	val selectedRaceDetails = internalSelectedRaceDetails as Observable<ItemState<out DndAbilitySource>>

	private lateinit var dataStore: DndRaceDataStore
	private lateinit var concurrency: Concurrency

	constructor()

	private constructor(parcel: Parcel) {
		selectionState = ItemStateUtils.readItemStateFromParcel(parcel)
	}

	override fun start(prerequisites: DndRacePrerequisites) {
		concurrency = prerequisites.concurrency
		dataStore = prerequisites.dataStore
		val racesFromState = selectionState.item?.options?.mapNotNull { it.item }
		if (racesFromState != null) {
			dataStore.restoreRaceList(racesFromState)
		}
		concurrency.runDiskOrNetwork(::doLoadAvailableRaces)
	}

	override fun refresh() {
		concurrency.runCalculation(::doUpdateRaceState) { internalStateChanges.onNext(selectionState) }
	}

	private suspend fun doLoadAvailableRaces() {
		val races = dataStore.getRaceList()
		selectionState = Normal(DndRaceSelection(races.map { Normal(it) }))
		externalStateChanges.onNext(selectionState)
	}

	private suspend fun doLoadRaceDetails() {
		val race = selectionState.item?.selectedItem?.item ?: return
		val details = dataStore.getRaceDetails(race)
		if (details == null) {
			logger.writeError("Unable to load details for $race")
			return
		}
		if (selectionState.item?.selectedItem?.item == race) {
			internalSelectedRaceDetails.onNext(Selected(details))
		}
	}

	private suspend fun doUpdateRaceState() = withContext(Dispatchers.Default) {
		val newState = when(val oldState = selectionState) {
			is Completed -> {
				if (oldState.item.selectedItem != null) {
					oldState
				} else {
					Normal(oldState.item)
				}
			}
			is Normal -> {
				if (oldState.item.selectedItem != null) {
					Completed(oldState.item)
				} else {
					oldState
				}
			}
			else -> oldState
		}
		when (newState) {
			is Completed -> concurrency.runDiskOrNetwork(::doLoadRaceDetails)
			else -> internalSelectedRaceDetails.onNext(Removed)
		}
		selectionState = newState
	}

	override fun writeToParcel(dest: Parcel?, flags: Int) {
		dest?.let {
			ItemStateUtils.writeItemStateToParcel(selectionState, it)
		}
	}

	override fun describeContents(): Int = 0

	companion object CREATOR : Parcelable.Creator<DndRaces> {

		override fun createFromParcel(source: Parcel): DndRaces {
			return DndRaces(source)
		}

		override fun newArray(size: Int): Array<DndRaces?> {
			return arrayOfNulls(size)
		}
	}

}
