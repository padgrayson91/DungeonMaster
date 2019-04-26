package com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.PageAction
import com.tendebit.dungeonmaster.charactercreation3.Undefined
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation3.proficiency.ProficiencyProvider
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

/**
 * ViewModel for the entire proficiency selection process. This ViewModel indicates how many
 * pages
 */
class DndProficiencySelectionViewModel(private val provider: ProficiencyProvider) {

	private val proficiencyOptionsDisposable: CompositeDisposable = CompositeDisposable()
	private var childUpdateDisposable: Disposable? = null

	var showLoading = provider.state is Undefined
		private set
	val children = ArrayList<DndProficiencyGroupViewModel>(provider.state.item?.groupStates?.map { DndProficiencyGroupViewModel(it) } ?: emptyList())
	var pageCount = children.size
		private set

	private val internalChanges = BehaviorSubject.create<DndProficiencySelectionViewModel>()
	val changes = internalChanges as Observable<DndProficiencySelectionViewModel>

	init {
		onStateChangedExternally(provider.state)
		proficiencyOptionsDisposable.addAll(
				provider.externalStateChanges.subscribe { onStateChangedExternally(it) },
				provider.internalStateChanges.subscribe { onStateChangedInternally(it) })
	}

	fun getPageActions(forChild: Int): List<PageAction> {
		return if (forChild < pageCount - 1) {
			// For any page besides the last one, user may always navigate forward and backward
			listOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_NEXT)
		} else {
			// For the last page, user can only navigate forward if all groups have been filled out
			if (provider.state is Completed) {
				listOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_NEXT)
			} else {
				listOf(PageAction.NAVIGATE_BACK)
			}
		}
	}

	private fun onStateChangedExternally(newState: ItemState<out DndProficiencySelection>) {
		children.clear()
		children.addAll(newState.item?.groupStates?.map { DndProficiencyGroupViewModel(it) } ?: emptyList())
		subscribeToSelection(newState.item)
		updateViewModelValues(newState)
	}


	private fun onStateChangedInternally(newState: ItemState<out DndProficiencySelection>) {
		updateViewModelValues(newState)
	}

	private fun updateViewModelValues(state: ItemState<out DndProficiencySelection>) {
		showLoading = state is Undefined
		pageCount = children.size
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
		provider.refreshProficiencyState()
	}

}
