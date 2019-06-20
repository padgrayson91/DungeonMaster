package com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencyGroup
import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import com.tendebit.dungeonmaster.core.model.Completed
import com.tendebit.dungeonmaster.core.model.ItemState
import com.tendebit.dungeonmaster.core.viewmodel3.MultiSelectViewModel
import com.tendebit.dungeonmaster.core.viewmodel3.Page
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

/**
 * ViewModel for displaying a selection of proficiencies
 */
class DndProficiencyGroupViewModel(initialState: ItemState<out DndProficiencyGroup>, private val concurrency: Concurrency) : MultiSelectViewModel, Page {

	private var internalState = initialState
	var state: ItemState<out DndProficiencyGroup>
		get() = internalState
		set(value) { onStateChanged(value) }
	private val internalChanges = PublishSubject.create<DndProficiencyGroupViewModel>()

	/**
	 * Emits whenever a change occurs to the relevant fields of this ViewModel
	 */
	override val changes = internalChanges as Observable<DndProficiencyGroupViewModel>

	/**
	 * The [DndProficiencyViewModel] items which serve as children of this ViewModel, representing
	 * each individual proficiency
	 */
	override val children = ArrayList(state.item?.options?.map { DndProficiencyViewModel(it, concurrency) } ?: emptyList())

	/**
	 * The number of remaining choices for this group, which should be displayed by the view
	 */
	override val remainingChoices: Int
		get() = state.item?.remainingChoices ?: 0

	override val isComplete: Boolean
		get() = state is Completed

	private var groupDisposable: Disposable? = null
	private var childDisposable = CompositeDisposable()

	init {
		subscribeToGroup()
		subscribeToChildren()
	}

	private fun onStateChanged(state: ItemState<out DndProficiencyGroup>) {
		internalState = state
		updateChildrenForState(state)
		subscribeToGroup()
		subscribeToChildren()
		internalChanges.onNext(this)
	}

	private fun updateChildrenForState(state: ItemState<out DndProficiencyGroup>) {
		state.item?.options?.forEachIndexed { index, item ->
			children[index].state = item
		}
	}

	private fun subscribeToGroup() {
		groupDisposable?.dispose()
		groupDisposable = state.item?.selectionChanges?.subscribe {
			children[it.index].state = it.state
			internalChanges.onNext(this)
		}
	}

	private fun subscribeToChildren() {
		childDisposable.dispose()
		childDisposable = CompositeDisposable()
		for (child in children.withIndex()) {
			childDisposable.add(child.value.selection.subscribe {
				if (it) {
					state.item?.select(child.index)
				} else {
					state.item?.deselect(child.index)
				}
			})
		}
	}

	override fun toString(): String {
		return "ViewModel for $state"
	}
}
