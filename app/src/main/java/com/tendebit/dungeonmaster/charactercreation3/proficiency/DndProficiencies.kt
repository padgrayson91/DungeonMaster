package com.tendebit.dungeonmaster.charactercreation3.proficiency

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.DndProficiencyDataStore
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.DndProficiencyDataStoreImpl
import com.tendebit.dungeonmaster.charactercreation3.proficiency.data.network.DndProficiencyApiConnection
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmaster.core.model.Completed
import com.tendebit.dungeonmaster.core.model.ItemState
import com.tendebit.dungeonmaster.core.model.ItemStateUtils
import com.tendebit.dungeonmaster.core.model.Normal
import com.tendebit.dungeonmaster.core.model.Removed
import com.tendebit.dungeonmaster.core.model.Selection
import com.tendebit.dungeonmaster.core.model.Undefined
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.BiFunction
import io.reactivex.subjects.PublishSubject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class DndProficiencies : ProficiencyProvider, Parcelable {

	override var state: ItemState<out DndProficiencySelection> = Removed

	override val internalStateChanges = PublishSubject.create<ItemState<out DndProficiencySelection>>()
	override val externalStateChanges = PublishSubject.create<ItemState<out DndProficiencySelection>>()

	private var disposable: Disposable? = null

	private lateinit var dataStore: DndProficiencyDataStore

	constructor()

	constructor(parcel: Parcel) {
		state = ItemStateUtils.readItemStateFromParcel(parcel)
	}

	override fun start(prerequisites: ProficiencyPrerequisites, scope: CoroutineScope) {
		dataStore = DndProficiencyDataStoreImpl(DndProficiencyApiConnection.Impl(), prerequisites.storage)
		val prereqObservable: Observable<Pair<ItemState<out Selection<DndCharacterClass>>, ItemState<out Selection<DndRace>>>> = Observable.combineLatest(
				prerequisites.classSelections, prerequisites.raceSelections,
				BiFunction { t1: ItemState<out Selection<DndCharacterClass>>, t2: ItemState<out Selection<DndRace>> -> Pair(t1, t2) })
		disposable = prereqObservable.subscribe {
			scope.launch(context = Dispatchers.IO) {
				updateStateForPrerequisiteChange(it.first, it.second)
			}
		}
	}

	override fun refreshProficiencyState() {
		val oldState = state
		logger.writeDebug("Performing state check. Current state is $oldState")
		val newState = when (oldState) {
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

	private suspend fun updateStateForPrerequisiteChange(classSelection: ItemState<out Selection<DndCharacterClass>>, raceSelection: ItemState<out Selection<DndRace>>) = coroutineScope {
		logger.writeDebug("Either class selection $classSelection or race selection $raceSelection has changed")
		if (classSelection !is Completed && raceSelection !is Completed) {
			state = Removed
			externalStateChanges.onNext(state)
			return@coroutineScope
		}

		val selectedClass = classSelection.item?.selectedItem?.item
		val selectedRace = raceSelection.item?.selectedItem?.item

		if (selectedClass == null && selectedRace == null) {
			state = Removed
			externalStateChanges.onNext(state)
			return@coroutineScope
		}

		state = Undefined
		externalStateChanges.onNext(state)

		val classProficiencies = async { if (selectedClass == null) emptyList() else dataStore.getProficiencyList(selectedClass) }
		val raceProficiencies = async { if (selectedRace == null) emptyList() else dataStore.getProficiencyList(selectedRace) }
		state = Normal(DndProficiencySelection(ArrayList<DndProficiencyGroup>().apply { addAll(classProficiencies.await()); addAll(raceProficiencies.await())}))
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
