package com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.Selected
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.logger
import com.tendebit.dungeonmaster.core.viewmodel3.SelectableViewModel
import com.tendebit.dungeonmaster.core.viewmodel3.TextTypes
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class DndCharacterClassViewModel(initialState: ItemState<out DndCharacterClass>) : SelectableViewModel<DndCharacterClass> {

	private var internalState = initialState
	override var state: ItemState<out DndCharacterClass>
		get() = internalState
		set(value) = onStateChanged(value)
	override val textType: TextTypes
		get() = when(state) {
			is Selected -> TextTypes.SELECTED
			else -> TextTypes.NORMAL
		}
	override val text = state.item?.name
	override val changes: Observable<DndCharacterClassViewModel> = Observable.just(this)
	private val internalSelection = BehaviorSubject.create<Boolean>()
	internal val selection = internalSelection.distinct()

	private fun onStateChanged(state: ItemState<out DndCharacterClass>) {
		internalState = state
	}

	override fun onClick() {
		logger.writeDebug("Clicked ${state.item}")
		internalSelection.onNext(state !is Selected)
	}

}
