package com.tendebit.dungeonmastercore.viewmodel3

import android.os.Parcelable
import io.reactivex.Observable

interface SingleSelectViewModel<T> : ViewModel, Clearable {
	val children: List<SelectableViewModel<T>>
	val itemCount: Int
	val showLoading: Boolean
	override val changes: Observable<out SingleSelectViewModel<T>>
	val itemChanges: Observable<Int>

	fun getInstanceState(): Parcelable?

}
