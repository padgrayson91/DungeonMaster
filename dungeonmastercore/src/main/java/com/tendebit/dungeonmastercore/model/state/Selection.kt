package com.tendebit.dungeonmastercore.model.state

import io.reactivex.Observable

interface Selection<T> {

	val options: List<ItemState<out T>>
	val selectedItem: Selected<out T>?

	/**
	 * An [Observable] which will emit whenever the [ItemState] for any item in this group changes
	 */
	val selectionChanges: Observable<ListItemState<T>>

	/**
	 * An [Observable] which will emit [ItemState] changes driven by selections coming from within this group
	 * @see select
	 * @see deselect
	 */
	val outboundSelectionChanges: Observable<ListItemState<T>>

	fun select(index: Int)
	fun deselect(index: Int)

}
