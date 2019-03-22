package com.tendebit.dungeonmaster.core.viewmodel2

import com.tendebit.dungeonmaster.core.Id
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import com.tendebit.dungeonmaster.core.blueprint.requirement.SelectionRequirement
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class SelectionViewModel<T>(override val id: Id): ViewModel {

	private var internalSelectionRequirement: SelectionRequirement<T>? = null
	var selectionRequirement
		get() = internalSelectionRequirement
		set(value) { onNewSelectionRequirement(value); internalSelectionRequirement = value; loading = value == null }

	// Internal data at rest (should not be modified, use private setters for public data at rest)
	private var internalOptions = selectionRequirement?.choices ?: emptyList() // TODO: map to UI model
	private var internalSelection = internalSelectionRequirement?.item

	// Internal subjects for publishing data
	private val internalOptionChanges = BehaviorSubject.create<List<T>>()
	private val internalLoadingChanges = BehaviorSubject.create<Boolean>()
	private val internalSelectionChanges = BehaviorSubject.create<T>()

	// Public observables to listen for changes
	val optionChanges = internalOptionChanges as Observable<List<T>>
	val selectionChanges = internalSelectionChanges as Observable<T>
	val loadingChanges = internalLoadingChanges as Observable<Boolean>

	private var selectionDisposable: Disposable? = null
	private var optionsDisposable: Disposable? = null

	// Public data at rest
	var loading: Boolean
		get() = selectionRequirement == null
		private set(value) { internalLoadingChanges.onNext(value) }
	var options
		get() = internalOptions
		set(value) { internalOptions = value; internalOptionChanges.onNext(value) }
	var selection
		get() = internalSelection // TODO: map to UI model
		set(value) { internalSelection = value; if (value != null) internalSelectionChanges.onNext(value) }

	init {
		options = internalOptions
		selection = internalSelection
		loading = selectionRequirement == null
		subscribeToSelectionRequirement(selectionRequirement)
	}

	fun clear() {
		clearSelectionSubscription()
		clearOptionsSubscription()
	}

	private fun clearSelectionSubscription() {
		selectionDisposable?.dispose()
		selectionDisposable = null
	}

	private fun clearOptionsSubscription() {
		optionsDisposable?.dispose()
		optionsDisposable = null
	}

	private fun subscribeToSelectionRequirement(selectionRequirement: SelectionRequirement<T>?) {
		clearSelectionSubscription()
		selectionDisposable = selectionRequirement?.statusChanges?.subscribe {
			when (it!!) {
				Requirement.Status.NOT_SET -> TODO() // Emit error? This should never happen
				Requirement.Status.FULFILLED -> selection = selectionRequirement.item
				Requirement.Status.NOT_FULFILLED -> TODO() // Emit error? This should never happen
			}
		}
	}

	private fun onNewSelectionRequirement(requirement: SelectionRequirement<T>?) {
		val choices = requirement?.choices
		if (options != choices && choices != null) {
			options = choices
		}

		val newSelection = requirement?.item
		if (selection != newSelection) {
			selection = newSelection
		}

		subscribeToSelectionRequirement(requirement)
	}

	fun select(choice: T) {
		selectionRequirement?.update(choice)
	}

}
