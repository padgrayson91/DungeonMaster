package com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencyGroup
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

/**
 * ViewModel for displaying a selection of proficiencies
 */
class DndProficiencyGroupViewModel(initialState: ItemState<out DndProficiencyGroup>) {

	private var internalState = initialState
	internal var state: ItemState<out DndProficiencyGroup>
		get() = internalState
		set(value) { onStateChanged(value) }
	private val internalChanges = PublishSubject.create<DndProficiencyGroupViewModel>()

	/**
	 * Emits whenever a change occurs to the relevant fields of this ViewModel
	 */
	val changes = internalChanges as Observable<DndProficiencyGroupViewModel>

	/**
	 * The [DndProficiencyViewModel] items which serve as children of this ViewModel, representing
	 * each individual proficiency
	 */
	val children = ArrayList(getChildrenForState(initialState))

	/**
	 * The number of remaining choices for this group, which should be displayed by the view
	 */
	val remainingChoices: Int
		get() = state.item?.remainingChoices ?: 0

	private var groupDisposable: Disposable? = null
	private var childDisposable = CompositeDisposable()

	init {
		subscribeToGroup()
		subscribeToChildren()
	}

	private fun onStateChanged(state: ItemState<out DndProficiencyGroup>) {
		internalState = state
		// FIXME: children may not need to be cleared for all state changes
		children.clear()
		children.addAll(getChildrenForState(state))
		subscribeToGroup()
		subscribeToChildren()
		internalChanges.onNext(this)
	}

	private fun getChildrenForState(state: ItemState<out DndProficiencyGroup>): List<DndProficiencyViewModel> {
		return state.item?.options?.map { DndProficiencyViewModel(it) } ?: emptyList()
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

}
