package com.tendebit.dungeonmaster.charactercreation3.race.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.race.DndRace
import com.tendebit.dungeonmaster.core.model.ItemState
import com.tendebit.dungeonmaster.core.model.Selected
import com.tendebit.dungeonmaster.core.viewmodel3.SelectableViewModel
import com.tendebit.dungeonmaster.core.viewmodel3.TextTypes
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
	internal val selection = internalSelection.distinct()

	private fun onStateChanged(state: ItemState<out DndRace>) {
		internalState = state
	}

	override fun onClick() {
		internalSelection.onNext(state !is Selected)
	}

}
