package com.tendebit.dungeonmaster.charactercreation.pages.classselection

import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class ClassSelectionViewModel2(selectionRequirement: DndClassRequirement?) {

	private var internalSelectionRequirement = selectionRequirement
	var selectionRequirement
		set(value) { onNewSelectionRequirement(value); internalSelectionRequirement = value; loading = value == null }
		get() = internalSelectionRequirement

	// Internal data at rest (should not be modified, use private setters for public data at rest)
	private var internalOptions = selectionRequirement?.choices ?: emptyList() // TODO: map to UI model
	private var internalSelection = internalSelectionRequirement?.item

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


	// Internal subjects for publishing data
	private val internalOptionChanges = BehaviorSubject.create<List<DndClass>>()
	private val internalLoadingChanges = BehaviorSubject.create<Boolean>()
	private val internalSelectionChanges = BehaviorSubject.create<DndClass>()

	// Public observables to listen for changes
	val optionChanges = internalOptionChanges as Observable<List<DndClass>>
	val selectionChanges = internalSelectionChanges as Observable<DndClass>
	val loadingChanges = internalLoadingChanges as Observable<Boolean>

	private var selectionDisposable: Disposable? = null
	private var optionsDisposable: Disposable? = null


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

	private fun subscribeToSelectionRequirement(selectionRequirement: DndClassRequirement?) {
		clearSelectionSubscription()
		selectionDisposable = selectionRequirement?.statusChanges?.subscribe {
			when (it!!) {
				Requirement.Status.NOT_SET -> TODO() // Emit error? This should never happen
				Requirement.Status.FULFILLED -> selection = selectionRequirement.item
				Requirement.Status.NOT_FULFILLED -> TODO() // Emit error? This should never happen
			}
		}
	}

	private fun onNewSelectionRequirement(requirement: DndClassRequirement?) {
		val choices = requirement?.choices
		if (options != choices && choices != null) {
			options = choices
		}

		val newSelection = requirement?.item
		if (selection != newSelection) {
			selection = newSelection
		}
	}

	fun select(dndClass: DndClass) {
		selectionRequirement?.update(dndClass)
	}

}
