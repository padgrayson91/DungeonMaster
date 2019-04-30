package com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.Undefined
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation3.proficiency.ProficiencyProvider
import com.tendebit.dungeonmaster.charactercreation3.viewmodel.PageSection
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

/**
 * ViewModel for the entire proficiency selection process. This ViewModel indicates how many
 * pages
 */
class DndProficiencySelectionViewModel(private val provider: ProficiencyProvider) : PageSection {

	private val proficiencyOptionsDisposable: CompositeDisposable = CompositeDisposable()
	private var childUpdateDisposable: Disposable? = null

	override var showLoading = provider.state is Undefined
		private set
	override val pages = ArrayList<DndProficiencyGroupViewModel>(provider.state.item?.groupStates?.map { DndProficiencyGroupViewModel(it) } ?: emptyList())
	override var pageCount = pages.size
		private set
	override val isComplete: Boolean
		get() = provider.state is Completed

	override val changes = BehaviorSubject.create<DndProficiencySelectionViewModel>()

	override val pageAdditions = PublishSubject.create<Int>()
	override val pageRemovals = PublishSubject.create<Int>()

	init {
		onStateChangedExternally(provider.state)
		proficiencyOptionsDisposable.addAll(
				provider.externalStateChanges.subscribe { onStateChangedExternally(it) },
				provider.internalStateChanges.subscribe { onStateChangedInternally(it) })
	}

	private fun onStateChangedExternally(newState: ItemState<out DndProficiencySelection>) {
		pages.clear()
		pages.addAll(newState.item?.groupStates?.map { DndProficiencyGroupViewModel(it) } ?: emptyList())
		subscribeToSelection(newState.item)
		updateViewModelValues(newState)
	}


	private fun onStateChangedInternally(newState: ItemState<out DndProficiencySelection>) {
		updateViewModelValues(newState)
	}

	private fun updateViewModelValues(state: ItemState<out DndProficiencySelection>) {
		val oldPageCount = pageCount
		showLoading = state is Undefined
		pageCount = pages.size
		val pageDiff = pageCount - oldPageCount
		if (pageDiff > 0) {
			// New page count is higher, so pages were added
			// E.g. if old count is 1 and new count is 3, page diff is 2 and we emit page addition
			// at indices 1 and 2
			for (i in oldPageCount until oldPageCount + pageDiff) {
				pageAdditions.onNext(i)
			}
		} else if (pageDiff < 0) {
			// New page count is lower, so pages were removed
			// E.g. if old count is 3 and new count is 1, page diff is -2 and we emit page removal
			// at indices 2 and 1 (order is reversed since removing index 1 first would move index 2 to index 1)
			for (i in oldPageCount - 1 downTo oldPageCount + pageDiff) {
				pageRemovals.onNext(i)
			}
		}
		changes.onNext(this)
	}

	private fun subscribeToSelection(selection: DndProficiencySelection?) {
		childUpdateDisposable?.dispose()
		childUpdateDisposable = selection?.stateChanges?.subscribe {
			pages[it.index].state = it.state
			checkForCompletion()
		}
	}

	private fun checkForCompletion() {
		provider.refreshProficiencyState()
	}

}
