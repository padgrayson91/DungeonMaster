package com.tendebit.dungeonmastercore.viewmodel3

import com.tendebit.dungeonmastercore.model.state.ItemState

interface SelectableViewModel<T> : ViewModel {

	val state: ItemState<out T>
	val text: CharSequence?
	val textType: TextTypes

	fun onClick()


}