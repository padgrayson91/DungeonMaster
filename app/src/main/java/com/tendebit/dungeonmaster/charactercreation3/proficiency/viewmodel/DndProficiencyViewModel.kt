package com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.Locked
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Selected
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiency
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class DndProficiencyViewModel(initialState: ItemState<out DndProficiency>) {

	private var internalState = initialState
	internal var state: ItemState<out DndProficiency>
		get() = internalState
		set(value) { onStateChanged(value) }
	val enabled = state is Normal || state is Selected
	val checked = state is Selected || state is Locked
	val text = state.item?.name

	private val internalChanges = PublishSubject.create<DndProficiencyViewModel>()
	val changes = internalChanges as Observable<DndProficiencyViewModel>

	private val internalSelection = PublishSubject.create<Boolean>()
	internal val selection = internalSelection.distinct()

	fun changeSelection(selected: Boolean) {
		internalSelection.onNext(selected)
	}

	private fun onStateChanged(state: ItemState<out DndProficiency>) {
		internalState = state
		internalChanges.onNext(this)
	}

}