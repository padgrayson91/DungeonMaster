package com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel

import com.tendebit.dungeonmastercore.model.state.Disabled
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Locked
import com.tendebit.dungeonmastercore.model.state.Removed
import com.tendebit.dungeonmastercore.model.state.Selected
import com.tendebit.dungeonmastercore.viewmodel3.SelectableViewModel
import com.tendebit.dungeonmastercore.viewmodel3.TextTypes
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class DndAbilityDiceRollViewModel(initialState: ItemState<out Int>) : SelectableViewModel<Int> {

	override var state: ItemState<out Int> = initialState
		set(value) { field = value; onStateChanged() }
	override val changes = PublishSubject.create<DndAbilityDiceRollViewModel>()
	private val internalSelection = BehaviorSubject.create<Boolean>()
	internal val selection = internalSelection as Observable<Boolean>

	val showHighlight: Boolean
		get() = state is Selected
	val disabled: Boolean
		get() = state is Disabled || state is Locked
	val visibility: Boolean
		get() = state !is Removed
	override val text: CharSequence?
		get() = state.item?.toString()
	override val textType: TextTypes
		get() = if (disabled) TextTypes.DISABLED else if (showHighlight) TextTypes.SELECTED else TextTypes.NORMAL

	override fun onClick() {
		if (disabled) return
		internalSelection.onNext(state !is Selected)
	}

	private fun onStateChanged() {
		changes.onNext(this)
	}

}
