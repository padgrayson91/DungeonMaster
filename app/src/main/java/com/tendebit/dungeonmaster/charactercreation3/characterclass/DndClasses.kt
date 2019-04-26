package com.tendebit.dungeonmaster.charactercreation3.characterclass

import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Removed
import io.reactivex.subjects.PublishSubject

class DndClasses : ClassProvider {

	override var state: ItemState<out DndCharacterClassSelection> = Removed

	override val internalStateChanges = PublishSubject.create<ItemState<out DndCharacterClassSelection>>()
	override val externalStateChanges = PublishSubject.create<ItemState<out DndCharacterClassSelection>>()

	override fun refreshClassState() {
		val oldState = state
		val newState = when(oldState) {
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
		if (oldState != newState) {
			internalStateChanges.onNext(newState)
		}
	}

	private fun doLoadAvailableClasses() {
		TODO()
	}

}
