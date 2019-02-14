package com.tendebit.dungeonmaster.charactercreation.pages.classselection

import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class ClassSelectionViewModel2(requirement: DndClassRequirement) {

	private var internalRequirement = requirement
	var requirement
		set(value) { onNewRequirement(value); internalRequirement = value }
		get() = internalRequirement
	val options
			get() = internalRequirement.choices // TODO: map to UI model
	val selection
			get() = internalRequirement.item // TODO: map to UI model

	private val internalOptionChanges = BehaviorSubject.create<List<DndClass>>()
	val optionChanges = internalOptionChanges as Observable<List<DndClass>>

	private val internalSelectionChanges = BehaviorSubject.create<DndClass>()
	val selectionChanges = internalSelectionChanges as Observable<DndClass>

	private var mainDisposable: Disposable? = null

	init {
		internalOptionChanges.onNext(internalRequirement.choices)
		internalRequirement.item?.let { internalSelectionChanges.onNext(it) }
		mainDisposable = internalRequirement.statusChanges.subscribe {
			when (it) {
				Requirement.Status.NOT_SET -> TODO() // Emit error? This should never happen
				Requirement.Status.FULFILLED -> internalSelectionChanges.onNext(internalRequirement.item!!)
				Requirement.Status.NOT_FULFILLED -> TODO() // Emit error? This should never happen
				else -> TODO() // Emit error? This should never happen
			}
		}
	}

	fun clear() {
		mainDisposable?.dispose()
		mainDisposable = null
	}

	private fun onNewRequirement(requirement: DndClassRequirement) {
		if (options != requirement.choices) {
			internalOptionChanges.onNext(requirement.choices)
		}

		if (selection != requirement.item) {
			requirement.item?.let { internalSelectionChanges.onNext(it) }
		}
	}

	fun select(dndClass: DndClass) {
		internalRequirement.update(dndClass)
	}

}
