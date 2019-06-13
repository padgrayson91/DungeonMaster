package com.tendebit.dungeonmaster.charactercreation3.proficiency

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.DndProficiencyDataStoreImpl
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.network.DndProficiencyApiConnection
import com.tendebit.dungeonmaster.core.model.Completed
import com.tendebit.dungeonmaster.core.model.ItemState
import com.tendebit.dungeonmaster.core.model.ItemStateUtils
import com.tendebit.dungeonmaster.core.model.Normal
import com.tendebit.dungeonmaster.core.model.Removed
import com.tendebit.dungeonmaster.core.model.Selection
import com.tendebit.dungeonmaster.core.model.Undefined
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DndProficiencies : ProficiencyProvider, Parcelable {

	override var state: ItemState<out DndProficiencySelection> = Removed

	override val internalStateChanges = PublishSubject.create<ItemState<out DndProficiencySelection>>()
	override val externalStateChanges = PublishSubject.create<ItemState<out DndProficiencySelection>>()

	private var disposable: Disposable? = null

	private val dataStore = DndProficiencyDataStoreImpl(DndProficiencyApiConnection.Impl())

	constructor()

	constructor(parcel: Parcel) {
		state = ItemStateUtils.readItemStateFromParcel(parcel)
	}

	override fun start(prerequisites: ProficiencyPrerequisites, scope: CoroutineScope) {
		disposable = prerequisites.classSelections.subscribe {
			scope.launch(context = Dispatchers.IO) { updateStateForClassSelectionChange (it) }
		}
	}

	override fun refreshProficiencyState() {
		val oldState = state
		logger.writeDebug("Performing state check. Current state is $oldState")
		val newState = when(oldState) {
			is Normal -> {
				if (oldState.item.groupStates.all { it is Completed }) {
					Completed(oldState.item)
				} else {
					oldState
				}
			}
			is Completed -> {
				if (oldState.item.groupStates.all { it is Completed }) {
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

	private suspend fun updateStateForClassSelectionChange(selection: ItemState<out Selection<DndCharacterClass>>) {
		logger.writeDebug("Got a new class selection: $selection")
		when(selection) {
			is Completed -> doLoadProficienciesForSelectedClass(selection.item.selectedItem?.item)
			else -> {
				state = Removed
				externalStateChanges.onNext(state)
			}
		}
	}

	private suspend fun doLoadProficienciesForSelectedClass(dndClass: DndCharacterClass?) {
		if (dndClass == null) {
			if (state !is Removed) {
				state = Removed
				externalStateChanges.onNext(state)
			}
			return
		}

		state = Undefined
		externalStateChanges.onNext(state)

		val proficiencies = dataStore.getProficiencyList(dndClass)
		state = Normal(DndProficiencySelection(proficiencies))
		externalStateChanges.onNext(state)
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
