package com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel

import android.os.Parcelable
import io.reactivex.Observable

interface SingleSelectViewModel {
	val children: List<DndCharacterClassViewModel>
	val itemCount: Int
	val showLoading: Boolean
	val pageCount: Int
	val changes: Observable<out SingleSelectViewModel>
	val itemChanges: Observable<Int>

	fun clear()

	fun getInstanceState(): Parcelable?

}
