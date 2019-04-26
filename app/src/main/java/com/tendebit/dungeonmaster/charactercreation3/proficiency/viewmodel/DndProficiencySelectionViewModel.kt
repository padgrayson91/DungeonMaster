package com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.PageAction
import com.tendebit.dungeonmaster.charactercreation3.Removed
import com.tendebit.dungeonmaster.charactercreation3.Undefined
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation3.proficiency.ProficiencyProvider
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

/**
 * ViewModel for the entire proficiency selection process. This ViewModel indicates how many
 * pages
 */
class DndProficiencySelectionViewModel(private val provider: ProficiencyProvider) {

	private var state: ItemState<out DndProficiencySelection> = Removed
	private val proficiencyOptionsDisposable: Disposable
	private var childUpdateDisposable: Disposable? = null

	val showLoading: Boolean
			get() = state is Undefined
	val children = ArrayList<DndProficiencyGroupViewModel>()
	val pageCount: Int
		get() = children.size
	private val internalChanges = BehaviorSubject.create<DndProficiencySelectionViewModel>()
	val changes = internalChanges as Observable<DndProficiencySelectionViewModel>

	init {
		proficiencyOptionsDisposable = provider.proficiencyOptions.subscribe { onStateChangedExternally(it) }
	}

	fun getPageActions(forChild: Int): List<PageAction> {
		return if (forChild < pageCount - 1) {
			// For any page besides the last one, user may always navigate forward and backward
			listOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_NEXT)
		} else {
			// For the last page, user can only navigate forward if all groups have been filled out
			if (state is Completed) {
				listOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_NEXT)
			} else {
				listOf(PageAction.NAVIGATE_BACK)
			}
		}
	}

	private fun onStateChangedExternally(newState: ItemState<out DndProficiencySelection>) {
		state = newState

		// FIXME: children probably don't need to be changed with every state change
		children.clear()
		children.addAll(state.item?.groupStates?.map { DndProficiencyGroupViewModel(it) } ?: emptyList())
		subscribeToSelection(state.item)
		internalChanges.onNext(this)
	}

	private fun subscribeToSelection(selection: DndProficiencySelection?) {
		childUpdateDisposable?.dispose()
		childUpdateDisposable = selection?.stateChanges?.subscribe {
			children[it.index].state = it.state
			checkForCompletion()
		}
	}

	private fun checkForCompletion() {
		val newState = provider.refreshState()
		if (newState != state) {
			state = newState
			internalChanges.onNext(this)
		}
	}

}
