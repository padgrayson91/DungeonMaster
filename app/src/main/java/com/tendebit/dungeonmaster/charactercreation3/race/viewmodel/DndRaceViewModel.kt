package com.tendebit.dungeonmaster.charactercreation3.race.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.characterclass.logger
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Selected
import com.tendebit.dungeonmastercore.viewmodel3.SelectableViewModel
import com.tendebit.dungeonmastercore.viewmodel3.TextTypes
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

class DndRaceViewModel(initialState: ItemState<out DndRace>) : SelectableViewModel<DndRace> {

	private var internalState = initialState
	override var state: ItemState<out DndRace>
		get() = internalState
		set(value) = onStateChanged(value)
	override val textType: TextTypes
		get() = when(state) {
			is Selected -> TextTypes.SELECTED
			else -> TextTypes.NORMAL
		}
	override val text = state.item?.name
	override val changes: Observable<DndRaceViewModel> = Observable.just(this)
	private val internalSelection = PublishSubject.create<Boolean>()
	internal val selection = internalSelection

	private fun onStateChanged(state: ItemState<out DndRace>) {
		internalState = state
	}

	override fun onClick() {
		if (!internalSelection.hasObservers()) {
			logger.writeError("No listeners!")
		}
		internalSelection.onNext(state !is Selected)
	}

}
