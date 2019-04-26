package com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.Loading
import com.tendebit.dungeonmaster.charactercreation3.PageAction
import com.tendebit.dungeonmaster.charactercreation3.characterclass.ClassProvider
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClassSelection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

private const val PAGE_COUNT = 1 // Always 1 page for class selection

class DndCharacterClassSelectionViewModel(private val provider: ClassProvider) {

	private val classOptionsDisposable = CompositeDisposable()
	private var childUpdateDisposable: Disposable? = null
	private var childClickDisposable = CompositeDisposable()

	val children = ArrayList<DndCharacterClassViewModel>(provider.state.item?.options?.map { DndCharacterClassViewModel(it) } ?: emptyList())

	var itemCount: Int = children.size
		private set
	var showLoading: Boolean = provider.state is Loading
		private set
	val pageCount = PAGE_COUNT

	private val internalChanges = BehaviorSubject.create<DndCharacterClassSelectionViewModel>()
	val changes = internalChanges as Observable<DndCharacterClassSelectionViewModel>

	private val internalItemChanges = PublishSubject.create<Int>()
	val itemChanges = internalItemChanges as Observable<Int>

	init {
		onStateChangedExternally(provider.state)
		classOptionsDisposable.addAll(
				provider.externalStateChanges.subscribe { onStateChangedExternally(it) },
				provider.internalStateChanges.subscribe { onStateChangedInternally(it) })
	}

	fun getPageActions(): List<PageAction> {
		//User can only navigate forward if a selection has been made
		return if (provider.state is Completed) {
			listOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_NEXT)
		} else {
			listOf(PageAction.NAVIGATE_BACK)
		}
	}

	private fun onStateChangedExternally(newState: ItemState<out DndCharacterClassSelection>) {
		children.clear()
		children.addAll(newState.item?.options?.map { DndCharacterClassViewModel(it) } ?: emptyList())
		subscribeToSelection(newState.item)
		subscribeToChildren(newState)
		updateViewModelValues(newState)
	}

	private fun onStateChangedInternally(newState: ItemState<out DndCharacterClassSelection>) {
		updateViewModelValues(newState)
	}

	private fun updateViewModelValues(state: ItemState<out DndCharacterClassSelection>) {
		showLoading = state is Loading
		itemCount = children.size
	}

	private fun subscribeToSelection(selection: DndCharacterClassSelection?) {
		childUpdateDisposable?.dispose()
		childUpdateDisposable = selection?.selectionChanges?.subscribe {
			children[it.index].state = it.state
			internalItemChanges.onNext(it.index)
		}
	}

	private fun subscribeToChildren(state: ItemState<out DndCharacterClassSelection>) {
		childClickDisposable.dispose()
		childClickDisposable = CompositeDisposable()
		for (childItem in children.withIndex()) {
			val child = childItem.value
			childClickDisposable.add(child.selection.subscribe {
				if (it) state.item?.select(childItem.index)
				else state.item?.deselect(childItem.index)
				checkForCompletion()
			})
		}
	}

	private fun checkForCompletion() {
		provider.refreshClassState()
	}

}
