package com.tendebit.dungeonmaster.core.viewmodel3

import android.os.Parcelable
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassViewModel
import io.reactivex.Observable

interface SingleSelectViewModel : ViewModel, Clearable {
	val children: List<DndCharacterClassViewModel>
	val itemCount: Int
	val showLoading: Boolean
	val pageCount: Int
	override val changes: Observable<out SingleSelectViewModel>
	val itemChanges: Observable<Int>

	fun getInstanceState(): Parcelable?

}
