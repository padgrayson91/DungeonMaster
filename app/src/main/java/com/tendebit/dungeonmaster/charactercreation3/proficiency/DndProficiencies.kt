package com.tendebit.dungeonmaster.charactercreation3.proficiency

import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Removed
import com.tendebit.dungeonmaster.charactercreation3.Undefined
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class DndProficiencies(prerequisites: ProficiencyPrerequisites) : ProficiencyProvider {

	override var state: ItemState<out DndProficiencySelection> = Removed

	override val internalStateChanges = PublishSubject.create<ItemState<out DndProficiencySelection>>()
	override val externalStateChanges = PublishSubject.create<ItemState<out DndProficiencySelection>>()

	private val disposable: Disposable

	init {
		disposable = prerequisites.classSelections.subscribe { doLoadProficienciesForSelectedClass(it) }
	}

	override fun refreshProficiencyState() {
		val oldState = state
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

		if (oldState != newState) {
			internalStateChanges.onNext(state)
		}
	}

	private fun doLoadProficienciesForSelectedClass(dndClass: DndCharacterClass) {
		state = Undefined
		externalStateChanges.onNext(state)

		TODO()
	}


}