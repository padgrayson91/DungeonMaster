package com.tendebit.dungeonmaster.core.viewmodel3

import com.tendebit.dungeonmaster.charactercreation3.ItemState

interface SelectableViewModel<T> : ViewModel {

	val state: ItemState<out T>
	val text: CharSequence?
	val textType: TextTypes

	fun onClick()


}