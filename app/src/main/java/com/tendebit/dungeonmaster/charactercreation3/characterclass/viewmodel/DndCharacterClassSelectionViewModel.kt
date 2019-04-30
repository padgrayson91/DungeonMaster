package com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.Loading
import com.tendebit.dungeonmaster.charactercreation3.characterclass.ClassProvider
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClassSelection
import com.tendebit.dungeonmaster.charactercreation3.viewmodel.Page
import com.tendebit.dungeonmaster.charactercreation3.viewmodel.PageSection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

private const val PAGE_COUNT = 1 // Always 1 page for class selection

class DndCharacterClassSelectionViewModel(private val provider: ClassProvider) : SingleSelectViewModel, PageSection, Page {

	private val classOptionsDisposable = CompositeDisposable()
	private var childUpdateDisposable: Disposable? = null
	private var childClickDisposable = CompositeDisposable()

	override val pages = listOf(this)
	override val children = ArrayList<DndCharacterClassViewModel>(provider.state.item?.options?.map { DndCharacterClassViewModel(it) } ?: emptyList())

	override var itemCount: Int = children.size
		private set
	override var showLoading: Boolean = provider.state is Loading
		private set
	override val pageCount = PAGE_COUNT

	override val changes = BehaviorSubject.create<DndCharacterClassSelectionViewModel>()

	private val internalItemChanges = PublishSubject.create<Int>()
	override val itemChanges = internalItemChanges as Observable<Int>

	override val isComplete: Boolean
		get() = provider.state is Completed

	override val pageAdditions: Observable<Int> = Observable.just(0)
	override val pageRemovals: Observable<Int> = Observable.empty<Int>()

	init {
		onStateChangedExternally(provider.state)
		classOptionsDisposable.addAll(
				provider.externalStateChanges.subscribe { onStateChangedExternally(it) },
				provider.internalStateChanges.subscribe { onStateChangedInternally(it) })
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
		changes.onNext(this)
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
