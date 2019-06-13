package com.tendebit.dungeonmaster.charactercreation3.race

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.App
import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.ItemStateUtils
import com.tendebit.dungeonmaster.charactercreation3.Loading
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.race.data.DndRaceDataStore
import com.tendebit.dungeonmaster.charactercreation3.race.data.DndRaceDataStoreImpl
import com.tendebit.dungeonmaster.charactercreation3.race.data.network.DndRaceApiConnection
import com.tendebit.dungeonmaster.charactercreation3.race.data.storage.RoomRaceStorage
import com.tendebit.dungeonmaster.charactercreation3.storage.CharacterCreationDb
import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import com.tendebit.dungeonmaster.core.model.Selection
import com.tendebit.dungeonmaster.core.model.SelectionProvider
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DndRaces : SelectionProvider<DndRace>, Parcelable {

	override var state: ItemState<out Selection<DndRace>> = Loading

	override val internalStateChanges = PublishSubject.create<ItemState<out Selection<DndRace>>>()
	override val externalStateChanges = PublishSubject.create<ItemState<out Selection<DndRace>>>()

	private lateinit var dataStore: DndRaceDataStore
	private lateinit var concurrency: Concurrency

	constructor()

	private constructor(parcel: Parcel) {
		state = ItemStateUtils.readItemStateFromParcel(parcel)
	}

	override fun start(concurrency: Concurrency) {
		this.concurrency = concurrency
		dataStore = DndRaceDataStoreImpl(DndRaceApiConnection.Impl(), RoomRaceStorage(CharacterCreationDb.getInstance(App.instance.applicationContext), concurrency))
		@Suppress("UNCHECKED_CAST")
		val racesFromState = state.item?.options?.map { it.item }?.filter { it != null } as? List<DndRace>
		if (racesFromState != null) {
			dataStore.restoreRaceList(racesFromState)
		}
		concurrency.runDiskOrNetwork(::doLoadAvailableRaces)
	}

	override fun refresh() {
		concurrency.runCalculation(::doUpdateRaceState) { internalStateChanges.onNext(state) }
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
