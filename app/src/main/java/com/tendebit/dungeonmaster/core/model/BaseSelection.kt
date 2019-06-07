package com.tendebit.dungeonmaster.core.model

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.ListItemState
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.Selected
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject

abstract class BaseSelection<T> : Selection<T> {

	abstract override val options: MutableList<ItemState<out T>>
	override val selectedItem: Selected<out T>?
		get() = options.find { it is Selected } as? Selected<out T>
	private val indirectSelectionChanges = PublishSubject.create<ListItemState<T>>()
	private val directSelectionChanges = PublishSubject.create<ListItemState<T>>()
	override val selectionChanges: Observable<ListItemState<T>> = directSelectionChanges.mergeWith(indirectSelectionChanges)
	override val outboundSelectionChanges = directSelectionChanges as Observable<ListItemState<T>>
	private val selectedIndex: Int
		get() = options.indexOfFirst { it is Selected }

	override fun deselect(index: Int) {
		val target = options.getOrNull(index)

		if (target !is Selected) {
			throw IllegalArgumentException("Unable to deselect $target at $index")
		}

		val normalState = Normal(target.item)
		options[index] = normalState
		directSelectionChanges.onNext(ListItemState(index, normalState))
	}

	override fun select(index: Int) {
		val previousSelectionIndex = selectedIndex
		if (previousSelectionIndex == index) {
			return
		}

		val target = options.getOrNull(index)

		if (target !is Normal) {
			throw IllegalArgumentException("Unable to select $target at $index")
		}

		val selectedState = Selected(target.item)
		options[index] = selectedState
		directSelectionChanges.onNext(ListItemState(index, selectedState))

		// Remove the previous selection, if any
		if (previousSelectionIndex > -1) {
			val currentSelection = options[previousSelectionIndex] as Selected
			val updatedState = Normal(currentSelection.item)
			options[previousSelectionIndex] = updatedState
			indirectSelectionChanges.onNext(ListItemState(previousSelectionIndex, updatedState))
		}
	}

}
