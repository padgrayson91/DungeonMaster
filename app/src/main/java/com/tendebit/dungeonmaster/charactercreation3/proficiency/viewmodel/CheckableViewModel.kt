package com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel

import io.reactivex.Observable

interface CheckableViewModel {
	val enabled: Boolean
	val checked: Boolean
	val text: String?
	val changes: Observable<DndProficiencyViewModel>
	fun changeSelection(selected: Boolean)
}