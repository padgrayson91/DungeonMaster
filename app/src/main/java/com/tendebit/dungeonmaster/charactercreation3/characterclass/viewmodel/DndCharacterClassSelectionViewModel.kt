package com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel

import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.Completed
import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.Loading
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClassProvider
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClassSelection
import com.tendebit.dungeonmaster.charactercreation3.characterclass.logger
import com.tendebit.dungeonmaster.core.concurrency.Concurrency
import com.tendebit.dungeonmaster.core.viewmodel3.Page
import com.tendebit.dungeonmaster.core.viewmodel3.PageSection
import com.tendebit.dungeonmaster.core.viewmodel3.SingleSelectViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
private const val PAGE_COUNT = 1 // Always 1 page for class selection

class DndCharacterClassSelectionViewModel(private val provider: DndCharacterClassProvider, private val concurrency: Concurrency) : SingleSelectViewModel<DndCharacterClass>, PageSection, Page {

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

	override val changes = PublishSubject.create<DndCharacterClassSelectionViewModel>()

	private val internalItemChanges = PublishSubject.create<Int>()
	override val itemChanges = internalItemChanges as Observable<Int>

	override val isComplete: Boolean
		get() = provider.state is Completed

	override val pageAdditions: Observable<Int> = Observable.empty()
	override val pageRemovals: Observable<Int> = Observable.empty()

	init {
		onStateChangedExternally(provider.state)
		classOptionsDisposable.addAll(
				provider.externalStateChanges.subscribe { onStateChangedExternally(it) },
				provider.internalStateChanges.subscribe { onStateChangedInternally(it) })
	}

	override fun clear() {
		logger.writeDebug("Clearing resources")
		classOptionsDisposable.dispose()
		childClickDisposable.dispose()
		childUpdateDisposable?.dispose()
	}

	override fun getInstanceState(): Parcelable? = provider as? Parcelable

	private fun onStateChangedExternally(newState: ItemState<out DndCharacterClassSelection>) {
		logger.writeDebug("Got external state: $newState")
		concurrency.runCalculation({
			children.clear()
			children.addAll(newState.item?.options?.map { DndCharacterClassViewModel(it) } ?: emptyList())
			subscribeToSelection(newState.item)
			subscribeToChildren(newState)
			updateViewModelValues(newState)
		})
	}

	private fun onStateChangedInternally(newState: ItemState<out DndCharacterClassSelection>) {
		logger.writeDebug("Got internal state: $newState")
		updateViewModelValues(newState)
	}

	private fun updateViewModelValues(state: ItemState<out DndCharacterClassSelection>) {
		showLoading = state is Loading
		itemCount = children.size
		concurrency.runImmediate { changes.onNext(this@DndCharacterClassSelectionViewModel) }
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
				logger.writeDebug("Got selection change for ${child.state.item} to $it")
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
