package com.tendebit.dungeonmaster.charactercreation3.ability.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.abilitycore.AbilityProvider
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySelection
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.logger
import com.tendebit.dungeonmastercore.concurrency.Concurrency
import com.tendebit.dungeonmastercore.model.state.Completed
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Loading
import com.tendebit.dungeonmastercore.model.state.Undefined
import com.tendebit.dungeonmastercore.model.state.Waiting
import com.tendebit.dungeonmastercore.viewmodel3.Clearable
import com.tendebit.dungeonmastercore.viewmodel3.Page
import com.tendebit.dungeonmastercore.viewmodel3.PageSection
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

private const val ABILITY_PAGE_COUNT = 1 // Only ever 1 page for ability selection

class DndAbilitySelectionViewModel(private val provider: AbilityProvider, private val concurrency: Concurrency) : Page, PageSection, Clearable {

	override val changes = BehaviorSubject.create<DndAbilitySelectionViewModel>()
	override var isComplete: Boolean = provider.state is Completed
		private set
	override var showLoading  = provider.state is Loading || provider.state is Waiting
		private set
	var children = provider.state.item?.options?.map { DndAbilitySlotViewModel(it) } ?: emptyList()
	override val pages: List<Page> = listOf(this)
	override val pageCount: Int = ABILITY_PAGE_COUNT
	override val pageAdditions: Observable<Int> = Observable.empty()
	override val pageRemovals: Observable<Int> = Observable.empty()
	private val internalAbilitySlotChanges = PublishSubject.create<Int>()
	val abilitySlotChanges = internalAbilitySlotChanges as Observable<Int>
	var rolls: DndAbilityDiceRollSelectionViewModel? = null
		private set

	private var childUpdateDisposable: Disposable? = null
	private var rollClickDisposable: Disposable? = null
	private var mainDisposable: Disposable? = null

	init {
		subscribeToProvider(provider)
		onStateChangedExternally(provider.state)
	}

	override fun clear() {
		mainDisposable?.dispose()
		childUpdateDisposable?.dispose()
	}

	fun onClickRoll() {
		provider.state.item?.autoRoll()
	}

	private fun onStateChangedExternally(newState: ItemState<out DndAbilitySelection>) {
		logger.writeDebug("Got external state: $newState")
		updateChildren(newState.item)
		subscribeToSelection(newState.item)
		updateViewModelValues(newState)
	}

	private fun onStateChangedInternally(newState: ItemState<out DndAbilitySelection>) {
		logger.writeDebug("Got internal state: $newState")
		updateViewModelValues(newState)
	}

	private fun updateChildren(selection: DndAbilitySelection?) {
		childUpdateDisposable?.dispose()
		children = selection?.options?.map { DndAbilitySlotViewModel(it) } ?: emptyList()
		if (selection == null) {
			return
		}
		childUpdateDisposable = CompositeDisposable().apply {
			children.forEachIndexed { index, child ->
				add(child.clicks.subscribe {
					selection.performAssignment(index)
					child.state = selection.options[index]
				})
				add(child.changes.subscribe { concurrency.runImmediate { internalAbilitySlotChanges.onNext(index) }})
			}
		}
	}

	private fun updateViewModelValues(state: ItemState<out DndAbilitySelection>) {
		showLoading = state is Undefined || state is Loading || state is Waiting
		checkForCompletion()
		concurrency.runImmediate { changes.onNext(this@DndAbilitySelectionViewModel) }
	}

	private fun subscribeToProvider(provider: AbilityProvider) {
		mainDisposable = CompositeDisposable().apply {
			addAll(provider.externalStateChanges.subscribe { onStateChangedExternally(it) },
					provider.internalStateChanges.subscribe { onStateChangedInternally(it) })
		}
	}

	private fun subscribeToSelection(selection: DndAbilitySelection?) {
		rollClickDisposable?.dispose()
		if (selection != null) {
			rolls = DndAbilityDiceRollSelectionViewModel(selection, concurrency)
			rollClickDisposable = CompositeDisposable().apply {
				rolls?.children?.forEachIndexed { index, roll ->
					add(roll.selection.subscribe { selection.performScoreSelection(index) })
				}
			}
		} else {
			rolls = null
		}
	}

	private fun checkForCompletion() {
		provider.refreshAbilityState()
		isComplete = provider.state is Completed
	}

}
