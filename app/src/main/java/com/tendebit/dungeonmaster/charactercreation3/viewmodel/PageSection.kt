package com.tendebit.dungeonmaster.charactercreation3.viewmodel

import io.reactivex.Observable

interface PageSection : Completable {
	val showLoading: Boolean
	val pages: List<Page>
	val pageCount: Int
	val changes: Observable<out PageSection>

	val pageAdditions: Observable<Int>
	val pageRemovals: Observable<Int>
}