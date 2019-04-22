package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.ListItemState
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Selected
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.lang.IllegalStateException

class DndProficiencySelection(forGroups: List<DndProficiencyGroup>) {

	val groupStates = ArrayList(forGroups.map { stateForGroup(it) })
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

	init {
		for (item in forGroups.withIndex()) {
			val group = item.value
			disposable.add(group.outboundSelectionChanges.subscribe {
				performStateCheck(item.index, item.value)
				when (it.state) {
					is Selected -> {
						for (otherGroup in forGroups - group) {
							otherGroup.onExternalSelection(it.state.item)
						}
					}
					is Normal -> {
						for (otherGroup in forGroups - group) {
							otherGroup.onExternalDeselection(it.state.item)
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

}