package com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.Selected
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import io.reactivex.subjects.PublishSubject

class DndCharacterClassViewModel(initialState: ItemState<out DndCharacterClass>) {

	enum class TextTypes {
		SELECTED,
		NORMAL
	}

	private var internalState = initialState
	var state: ItemState<out DndCharacterClass>
		get() = internalState
		set(value) { onStateChanged(value) }
	val textType = when(state) {
		is Selected -> TextTypes.SELECTED
		else -> TextTypes.NORMAL
	}
	val text = state.item?.name

	private val internalSelection = PublishSubject.create<Boolean>()
	internal val selection = internalSelection.distinct()

	private fun onStateChanged(state: ItemState<out DndCharacterClass>) {
		internalState = state
	}

	fun onClick() {
		internalSelection.onNext(state !is Selected)
	}

}
