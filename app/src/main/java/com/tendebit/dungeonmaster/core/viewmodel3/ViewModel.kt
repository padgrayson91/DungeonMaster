package com.tendebit.dungeonmaster.core.viewmodel3

import io.reactivex.Observable

interface ViewModel {

	val changes: Observable<out ViewModel>

}
