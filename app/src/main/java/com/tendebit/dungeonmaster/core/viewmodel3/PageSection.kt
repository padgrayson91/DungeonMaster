package com.tendebit.dungeonmaster.core.viewmodel3

import io.reactivex.Observable

interface PageSection : Completable, ViewModel {

	val showLoading: Boolean
	val pages: List<Page>
	val pageCount: Int
	override val changes: Observable<out PageSection>

	val pageAdditions: Observable<Int>
	val pageRemovals: Observable<Int>

}
