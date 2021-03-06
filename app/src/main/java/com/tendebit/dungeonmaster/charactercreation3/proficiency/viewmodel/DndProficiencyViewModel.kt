package com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiency
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.logger
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Locked
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Selected
import com.tendebit.dungeonmastercore.viewmodel3.CheckableViewModel
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class DndProficiencyViewModel(initialState: ItemState<out DndProficiency>, private val concurrency: Concurrency) : CheckableViewModel {

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
	internal val selection = internalSelection

	override fun changeSelection(selected: Boolean) {
		logger.writeDebug("Changed selection for $state to $selected")
		internalSelection.onNext(selected)
	}

	private fun onStateChanged(state: ItemState<out DndProficiency>) {
		internalState = state
		concurrency.runImmediate { internalChanges.onNext(this) }
	}

	override fun toString(): String {
		return "ViewModel for $state"
	}
}
