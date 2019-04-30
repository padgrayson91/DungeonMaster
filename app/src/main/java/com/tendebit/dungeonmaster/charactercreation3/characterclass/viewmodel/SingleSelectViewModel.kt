package com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel

import io.reactivex.Observable

interface SingleSelectViewModel {
	val children: List<DndCharacterClassViewModel>
	val itemCount: Int
	val showLoading: Boolean
	val pageCount: Int
	val changes: Observable<DndCharacterClassSelectionViewModel>
	val itemChanges: Observable<Int>
}