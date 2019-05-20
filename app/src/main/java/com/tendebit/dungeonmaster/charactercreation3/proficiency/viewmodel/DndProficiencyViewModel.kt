package com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.Locked
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Selected
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiency
import com.tendebit.dungeonmaster.core.viewmodel3.CheckableViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class DndProficiencyViewModel(initialState: ItemState<out DndProficiency>) : CheckableViewModel {

	private var internalState = initialState
	var state: ItemState<out DndProficiency>
		get() = internalState
		set(value) {
			onStateChanged(value)
		}
	override val enabled
		get() = state is Normal || state is Selected
	override val checked
		get() = state is Selected || state is Locked
	override val text = state.item?.name

	private val internalChanges = PublishSubject.create<DndProficiencyViewModel>()
	override val changes = internalChanges as Observable<DndProficiencyViewModel>

	private val internalSelection = PublishSubject.create<Boolean>()
	internal val selection = internalSelection.distinct()

	override fun changeSelection(selected: Boolean) {
		internalSelection.onNext(selected)
	}

	private fun onStateChanged(state: ItemState<out DndProficiency>) {
		internalState = state
		if (!internalChanges.hasObservers()) {
			throw IllegalStateException("Nobody is listening...")
		}
		internalChanges.onNext(this)
	}

}
