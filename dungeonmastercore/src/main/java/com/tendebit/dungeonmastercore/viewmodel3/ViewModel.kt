package com.tendebit.dungeonmastercore.viewmodel3

import io.reactivex.Observable

interface ViewModel {

	val changes: Observable<out ViewModel>

}
