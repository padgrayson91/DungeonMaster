package com.tendebit.dungeonmaster.core.model

import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.ListItemState
import com.tendebit.dungeonmaster.charactercreation3.Selected
import io.reactivex.Observable

interface Selection<T> : Parcelable {

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
