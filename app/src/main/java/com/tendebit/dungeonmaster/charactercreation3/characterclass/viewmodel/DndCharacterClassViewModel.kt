package com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.logger
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Selected
import com.tendebit.dungeonmastercore.viewmodel3.SelectableViewModel
import com.tendebit.dungeonmastercore.viewmodel3.TextTypes
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject

class DndCharacterClassViewModel(initialState: ItemState<out DndCharacterClass>) : SelectableViewModel<DndCharacterClass> {

	override var state: ItemState<out DndCharacterClass> = initialState
	override val textType: TextTypes
		get() = when(state) {
			is Selected -> TextTypes.SELECTED
			else -> TextTypes.NORMAL
		}
	override val text = state.item?.name
	override val changes: Observable<DndCharacterClassViewModel> = Observable.just(this)
	private val internalSelection = BehaviorSubject.create<Boolean>()
	internal val selection = internalSelection as Observable<Boolean>

	override fun onClick() {
		logger.writeDebug("Clicked ${state.item}")
		if (!internalSelection.hasObservers()) {
			logger.writeError("No listeners!")
		}
		internalSelection.onNext(state !is Selected)
	}

}
