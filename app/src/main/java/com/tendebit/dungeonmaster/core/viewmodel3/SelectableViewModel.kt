package com.tendebit.dungeonmaster.core.viewmodel3

import com.tendebit.dungeonmaster.core.model.state.ItemState

interface SelectableViewModel<T> : ViewModel {

	val state: ItemState<out T>
	val text: CharSequence?
	val textType: TextTypes

	fun onClick()


}