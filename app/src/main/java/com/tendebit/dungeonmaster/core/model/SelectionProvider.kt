package com.tendebit.dungeonmaster.core.model

import io.reactivex.Observable

interface SelectionProvider<T> {

	val state: ItemState<out Selection<T>>

	val externalStateChanges: Observable<ItemState<out Selection<T>>>
	val internalStateChanges: Observable<ItemState<out Selection<T>>>

	fun refresh()

}
