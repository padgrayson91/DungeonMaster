package com.tendebit.dungeonmaster.core.model

import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import io.reactivex.Observable

interface SelectionProvider<T> {

	val state: ItemState<out Selection<T>>

	val externalStateChanges: Observable<ItemState<out Selection<T>>>
	val internalStateChanges: Observable<ItemState<out Selection<T>>>

	fun refresh()

	fun start(concurrency: Concurrency)

}
