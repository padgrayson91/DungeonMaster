package com.tendebit.dungeonmaster.core.viewmodel3

import io.reactivex.Observable

interface MultiSelectViewModel : ViewModel {

	/**
	 * Emits whenever a change occurs to the relevant fields of this ViewModel
	 */
	override val changes: Observable<out MultiSelectViewModel>
	/**
	 * The [CheckableViewModel] items which serve as children of this ViewModel, representing
	 * each individual proficiency
	 */
	val children: List<CheckableViewModel>
	/**
	 * The number of remaining choices for this group, which should be displayed by the view
	 */
	val remainingChoices: Int

}
