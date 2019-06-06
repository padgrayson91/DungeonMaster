package com.tendebit.dungeonmaster.charactercreation3.race

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.ItemStateUtils
import com.tendebit.dungeonmaster.charactercreation3.Loading
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.race.data.DndRaceDataStoreImpl
import com.tendebit.dungeonmaster.charactercreation3.race.data.network.DndRaceApiConnection
import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DndRaces : DndRaceProvider, Parcelable {

	override var state: ItemState<out DndRaceSelection> = Loading

	override val internalStateChanges = PublishSubject.create<ItemState<out DndRaceSelection>>()
	override val externalStateChanges = PublishSubject.create<ItemState<out DndRaceSelection>>()

	private val dataStore = DndRaceDataStoreImpl(DndRaceApiConnection.Impl())
	private var concurrency: Concurrency? = null

	constructor()

	private constructor(parcel: Parcel) {
		state = ItemStateUtils.readItemStateFromParcel(parcel)
		@Suppress("UNCHECKED_CAST")
		val racesFromState = state.item?.options?.map { it.item }?.filter { it != null } as? List<DndRace>
		if (racesFromState != null) {
			dataStore.restoreRaceList(racesFromState)
		}
	}

	override fun start(concurrency: Concurrency) {
		this.concurrency = concurrency
		concurrency.runDiskOrNetwork(::doLoadAvailableRaces)
	}

	override fun refreshClassState() {
		concurrency?.runCalculation(::doUpdateRaceState) { internalStateChanges.onNext(state) }
	}

	private suspend fun doLoadAvailableRaces() {
		val races = dataStore.getRaceList()
		state = Normal(DndRaceSelection(races.map { Normal(it) }))
		externalStateChanges.onNext(state)
	}

	private suspend fun doUpdateRaceState() = withContext(Dispatchers.Default) {
		val newState = when(val oldState = state) {
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
		state = newState
	}

	override fun writeToParcel(dest: Parcel?, flags: Int) {
		dest?.let {
			ItemStateUtils.writeItemStateToParcel(state, it)
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
