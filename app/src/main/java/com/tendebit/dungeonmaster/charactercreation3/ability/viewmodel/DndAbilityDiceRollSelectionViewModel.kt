package com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel

import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.logger
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Loading
import com.tendebit.dungeonmastercore.model.state.Removed
import com.tendebit.dungeonmastercore.model.state.Selection
import com.tendebit.dungeonmastercore.model.state.SelectionProvider
import com.tendebit.dungeonmastercore.viewmodel3.SingleSelectViewModel
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject

class DndAbilityDiceRollSelectionViewModel(private val provider: SelectionProvider<Int>, private val concurrency: Concurrency) : SingleSelectViewModel<Int> {

	private val diceRollOptionsDisposable = CompositeDisposable()
	private var childUpdateDisposable: Disposable? = null
	private var childClickDisposable = CompositeDisposable()

	private val state
		get() = provider.selectionState
	override val children = ArrayList(state.item?.options?.map { DndAbilityDiceRollViewModel(it) } ?: emptyList())
	override var itemCount = children.size
	override var showLoading = state is Loading
	override val changes = PublishSubject.create<DndAbilityDiceRollSelectionViewModel>()
	override val itemChanges = PublishSubject.create<Int>()

	init {
		onStateChangedExternally(provider.selectionState)
		diceRollOptionsDisposable.addAll(
				provider.externalStateChanges.subscribe { onStateChangedExternally(it) },
				provider.internalStateChanges.subscribe { onStateChangedInternally(it) })
	}

	override fun getInstanceState(): Parcelable? = provider as? Parcelable

	override fun clear() {
		logger.writeDebug("Clearing resources")
		diceRollOptionsDisposable.dispose()
		childClickDisposable.dispose()
		childUpdateDisposable?.dispose()
	}

	private fun onStateChangedExternally(newState: ItemState<out Selection<Int>>) {
		logger.writeDebug("Got external state: $newState")
		concurrency.runCalculation({
			subscribeToSelection(newState.item)
			updateViewModelValues(newState)
		})
	}

	private fun subscribeToSelection(selection: Selection<Int>?) {
		childUpdateDisposable?.dispose()
		childUpdateDisposable = selection?.selectionChanges?.subscribe {
			children[it.index].state = it.state
			concurrency.runImmediate { itemChanges.onNext(it.index) }
		}
	}

	private fun subscribeToChildren(state: ItemState<out Selection<Int>>) {
		childClickDisposable.dispose()
		childClickDisposable = CompositeDisposable()
		for (childItem in children.withIndex()) {
			val child = childItem.value
			childClickDisposable.add(child.selection.subscribe {
				logger.writeDebug("Got selection change for ${child.state.item} to $it")
				if (child.state is Removed) {
					return@subscribe
				}
				if (it) state.item?.select(childItem.index)
				else state.item?.deselect(childItem.index)
			})
		}
	}

	private fun onStateChangedInternally(newState: ItemState<out Selection<Int>>) {
		logger.writeDebug("Got internal state: $newState")
		updateViewModelValues(newState)
	}

	private fun updateViewModelValues(state: ItemState<out Selection<Int>>) {
		showLoading = state is Loading
		children.clear()
		children.addAll(state.item?.options?.map { DndAbilityDiceRollViewModel(it) } ?: emptyList())
		subscribeToChildren(state)
		itemCount = children.size
		concurrency.runImmediate { changes.onNext(this@DndAbilityDiceRollSelectionViewModel) }
	}
}
