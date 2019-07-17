package com.tendebit.dungeonmaster.charactercreation3.characterclass

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.DndCharacterClassDataStore
import com.tendebit.dungeonmaster.charactercreation3.characterclass.data.DndClassPrerequisites
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.DelayedStart
import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.ItemStateUtils
import com.tendebit.dungeonmastercore.model.state.Loading
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Selection
import com.tendebit.dungeonmastercore.model.state.SelectionProvider
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DndClasses : SelectionProvider<DndCharacterClass>, DelayedStart<DndClassPrerequisites>, Parcelable {

	override var state: ItemState<out Selection<DndCharacterClass>> = Loading

	override val internalStateChanges = PublishSubject.create<ItemState<out Selection<DndCharacterClass>>>()
	override val externalStateChanges = PublishSubject.create<ItemState<out Selection<DndCharacterClass>>>()

	private lateinit var dataStore: DndCharacterClassDataStore
	private lateinit var concurrency: Concurrency

	constructor()

	private constructor(parcel: Parcel) {
		state = ItemStateUtils.readItemStateFromParcel(parcel)
		logger.writeDebug("Got $state from parcel")
	}

	override fun start(prerequisites: DndClassPrerequisites) {
		this.concurrency = prerequisites.concurrency
		dataStore = prerequisites.dataStore
		val classesFromState = state.item?.options?.mapNotNull { it.item }
		if (classesFromState != null) {
			logger.writeDebug("Had ${classesFromState.size} classes from a parcelized state")
			dataStore.restoreCharacterClassList(classesFromState)
			externalStateChanges.onNext(state)
			return
		}
		concurrency.runDiskOrNetwork(::doLoadAvailableClasses)
	}

	override fun refresh() {
		concurrency.runCalculation(::doUpdateClassState) { internalStateChanges.onNext(state) }
	}

	private suspend fun doLoadAvailableClasses() {
		val characterClasses = dataStore.getCharacterClassList()
		state = Normal(DndCharacterClassSelection(characterClasses.map { Normal(it) }))
		externalStateChanges.onNext(state)
	}

	private suspend fun doUpdateClassState() = withContext(Dispatchers.Default) {
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
		logger.writeDebug("Parcelizing $this")
		dest?.let {
			ItemStateUtils.writeItemStateToParcel(state, it)
		}
	}

	override fun describeContents(): Int = 0

	companion object CREATOR : Parcelable.Creator<DndClasses> {

		override fun createFromParcel(source: Parcel): DndClasses {
			return DndClasses(source)
		}

		override fun newArray(size: Int): Array<DndClasses?> {
			return arrayOfNulls(size)
		}
	}

}
