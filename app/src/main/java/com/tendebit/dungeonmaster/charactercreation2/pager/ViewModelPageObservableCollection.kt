package com.tendebit.dungeonmaster.charactercreation2.pager

import com.tendebit.dungeonmaster.charactercreation.viewpager.PageInsertion
import com.tendebit.dungeonmaster.charactercreation.viewpager.PageRemoval
import io.reactivex.Observable

interface ViewModelPageObservableCollection: ViewModelPageCollection {
	val pageAdditions: Observable<PageInsertion>
	val pageRemovals: Observable<PageRemoval>
}