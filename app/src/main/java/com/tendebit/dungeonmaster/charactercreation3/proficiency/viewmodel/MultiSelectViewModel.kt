package com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel

import io.reactivex.Observable

interface MultiSelectViewModel {
	/**
	 * Emits whenever a change occurs to the relevant fields of this ViewModel
	 */
	val changes: Observable<DndProficiencyGroupViewModel>
	/**
	 * The [DndProficiencyViewModel] items which serve as children of this ViewModel, representing
	 * each individual proficiency
	 */
	val children: List<DndProficiencyViewModel>
	/**
	 * The number of remaining choices for this group, which should be displayed by the view
	 */
	val remainingChoices: Int
}