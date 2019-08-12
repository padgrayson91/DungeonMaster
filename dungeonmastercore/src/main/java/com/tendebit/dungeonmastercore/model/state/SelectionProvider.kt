package com.tendebit.dungeonmastercore.model.state

import io.reactivex.Observable

interface SelectionProvider<T> {

	val selectionState: ItemState<out Selection<T>>

	val externalStateChanges: Observable<ItemState<out Selection<T>>>
	val internalStateChanges: Observable<ItemState<out Selection<T>>>

	fun refresh()

}
