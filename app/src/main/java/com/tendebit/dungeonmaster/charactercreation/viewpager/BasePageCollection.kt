package com.tendebit.dungeonmaster.charactercreation.viewpager

import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.LinkedList

class BasePageCollection private constructor(override val pages: MutableList<ViewModel>): ViewModelPageObservableCollection, MutableList<ViewModel> by pages {

	constructor(): this(LinkedList())

	private val pageChanges = PublishSubject.create<PageChange>()

	override val pageAdditions: Observable<PageInsertion> = pageChanges.ofType(PageInsertion::class.java)
	override val pageRemovals: Observable<PageRemoval> = pageChanges.ofType(PageRemoval::class.java)

	override fun removePages(range: IntRange) {
		for (i in range) pages.removeAt(range.first)
		pageChanges.onNext(PageRemoval(range))
	}

	override fun insertPage(viewModel: ViewModel, index: Int) {
		if (index >= size) {
			add(viewModel)
			pageChanges.onNext(PageInsertion(size - 1 until size, listOf(viewModel)))
		} else {
			add(index, viewModel)
			pageChanges.onNext(PageInsertion(index..index, listOf(viewModel)))
		}
	}

}
