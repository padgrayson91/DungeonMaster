package com.tendebit.dungeonmaster.charactercreation3.proficiencycore

import android.os.Parcel
import android.os.Parcelable
import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.ItemStateUtils
import com.tendebit.dungeonmastercore.model.state.ListItemState
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Selected
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject

class DndProficiencySelection : Parcelable {

	constructor(forGroups: List<DndProficiencyGroup>) {
		groupStates = ArrayList(forGroups.map { stateForGroup(it) })
		updateGroupsToMatchExternal(forGroups)
		subscribeToGroups(forGroups)
	}

	private constructor(parcel: Parcel) {
		groupStates = ArrayList(ItemStateUtils.readItemStateListFromParcel(parcel))
		subscribeToGroups(groupStates.filter { it.item != null }.map { it.item!! })
	}

	val groupStates: MutableList<ItemState<out DndProficiencyGroup>>
	val selections: List<DndProficiency>
		get() {
			return groupStates.asSequence()
					.filter { it is Normal || it is Completed }
					.map { it.item }
					.filter { it != null }
					.map { it!!.selections }
					.toList()
					.flatten()
					.map { it.item!! }
		}
	private val internalStateChanges = PublishSubject.create<ListItemState<DndProficiencyGroup>>()
	val stateChanges = internalStateChanges as Observable<ListItemState<DndProficiencyGroup>>
	private val disposable = CompositeDisposable()
	val isEmpty: Boolean
		get() = groupStates.isEmpty()

	private fun updateGroupsToMatchExternal(forGroups: List<DndProficiencyGroup>) {
		for (group in forGroups) {
			// Mark all items as deselected externally; external selections might be stale
			for (item in group.options) {
				group.onExternalDeselection(item.item ?: continue)
			}

			for (otherGroup in forGroups - group) {
				for (selectedItem in otherGroup.selections) {
					group.onExternalSelection(selectedItem.item!!)
				}
			}
		}
	}

	private fun subscribeToGroups(forGroups: List<DndProficiencyGroup>) {
		for (item in forGroups.withIndex()) {
			val group = item.value
			disposable.add(group.outboundSelectionChanges.subscribe {
				performStateCheck(item.index, item.value)
				when (val state = it.state) {
					is Selected -> {
						for (otherGroup in forGroups - group) {
							otherGroup.onExternalSelection(state.item)
						}
					}
					is Normal -> {
						for (otherGroup in forGroups - group) {
							otherGroup.onExternalDeselection(state.item)
						}
					}
					else -> {
						throw IllegalStateException("Had unknown state ${it.state} for proficiency group")
					}
				}
			})
		}
	}

	private fun stateForGroup(dndProficiencyGroup: DndProficiencyGroup): ItemState<DndProficiencyGroup> {
		return if (dndProficiencyGroup.remainingChoices == 0) {
			Completed(dndProficiencyGroup)
		} else {
			Normal(dndProficiencyGroup)
		}
	}

	private fun performStateCheck(index: Int, dndProficiencyGroup: DndProficiencyGroup) {
		val oldState = groupStates[index]
		val newState = stateForGroup(dndProficiencyGroup)
		if (oldState::class != newState::class) {
			groupStates[index] = newState
			internalStateChanges.onNext(ListItemState(index, newState))
		}
	}

	override fun writeToParcel(dest: Parcel?, flags: Int) {
		dest?.let {
			ItemStateUtils.writeItemStateListToParcel(groupStates, it)
		}
	}

	override fun describeContents(): Int = 0

	companion object CREATOR : Parcelable.Creator<DndProficiencySelection> {

		override fun createFromParcel(source: Parcel): DndProficiencySelection {
			return DndProficiencySelection(source)
		}

		override fun newArray(size: Int): Array<DndProficiencySelection?> {
			return arrayOfNulls(size)
		}
	}

}
