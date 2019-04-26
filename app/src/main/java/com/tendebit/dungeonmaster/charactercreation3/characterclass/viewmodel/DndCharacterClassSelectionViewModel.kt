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

	private var state: ItemState<out DndCharacterClassSelection> = Loading
	private val classOptionsDisposable: Disposable
	private var childUpdateDisposable: Disposable? = null
	private var childClickDisposable = CompositeDisposable()

	val showLoading: Boolean
		get() = state is Loading
	val children = ArrayList<DndCharacterClassViewModel>()
	val itemCount: Int
		get() = children.size
	val pageCount = PAGE_COUNT
	private val internalChanges = BehaviorSubject.create<DndCharacterClassSelectionViewModel>()
	val changes = internalChanges as Observable<DndCharacterClassSelectionViewModel>

	private val internalItemChanges = PublishSubject.create<Int>()
	val itemChanges = internalItemChanges as Observable<Int>

	init {
		classOptionsDisposable = provider.classOptions.subscribe { onStateChangedExternally(it) }
	}

	fun getPageActions(): List<PageAction> {
		//User can only navigate forward if a selection has been made
		return if (state is Completed) {
			listOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_NEXT)
		} else {
			listOf(PageAction.NAVIGATE_BACK)
		}
	}

	private fun onStateChangedExternally(newState: ItemState<out DndCharacterClassSelection>) {
		state = newState

		// FIXME: children probably don't need to be changed with every state change
		children.clear()
		children.addAll(state.item?.options?.map { DndCharacterClassViewModel(it) } ?: emptyList())
		subscribeToSelection(state.item)
		subscribeToChildren()
		internalChanges.onNext(this)
	}

	private fun subscribeToSelection(selection: DndCharacterClassSelection?) {
		childUpdateDisposable?.dispose()
		childUpdateDisposable = selection?.selectionChanges?.subscribe {
			children[it.index].state = it.state
			internalItemChanges.onNext(it.index)
		}
	}

	private fun subscribeToChildren() {
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
		val newState = provider.refreshState()
		if (newState != state) {
			state = newState
			internalChanges.onNext(this)
		}
	}

}
