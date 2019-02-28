package com.tendebit.dungeonmaster.charactercreation.viewpager

import io.reactivex.Observable

interface ViewModelPageObservableCollection: ViewModelPageCollection {
	val pageAdditions: Observable<PageInsertion>
	val pageRemovals: Observable<PageRemoval>
}