package com.tendebit.dungeonmaster.core.viewmodel3

import io.reactivex.Observable

interface CheckableViewModel : ViewModel {
	val enabled: Boolean
	val checked: Boolean
	val text: String?
	override val changes: Observable<out CheckableViewModel>
	fun changeSelection(selected: Boolean)
}