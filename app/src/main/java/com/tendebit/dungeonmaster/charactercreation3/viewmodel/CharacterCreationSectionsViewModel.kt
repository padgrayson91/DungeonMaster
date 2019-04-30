package com.tendebit.dungeonmaster.charactercreation3.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.PageAction
import com.tendebit.dungeonmaster.core.extensions.addOrInsert
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.LinkedList

class CharacterCreationSectionsViewModel(sections: List<PageSection>) {

	private val offsets = calculateOffsets(sections)
	private val pageActions = determinePageActions(sections)
	private var sectionsDisposable = CompositeDisposable()

	private val internalPageRemovals = PublishSubject.create<Int>()
	val pageRemovals = internalPageRemovals as Observable<Int>

	private val internalPageAdditions = PublishSubject.create<Int>()
	val pageAdditions = internalPageAdditions as Observable<Int>

	val pages = LinkedList(getUpdatedPages(sections))
	val pageCount: Int
		get() = pages.size

	var showLoading = getUpdatedLoadingState(sections)
		private set

	init {
		subscribeToSections(sections)
	}

	fun getPageActions(index: Int): List<PageAction> = pageActions[index]

	private fun determinePageActions(sections: List<PageSection>): MutableList<List<PageAction>> {
		val result = LinkedList<List<PageAction>>()
		for (sectionItem in sections.withIndex()) {
			val section = sectionItem.value
			result.addAll(section.pages.withIndex().map { determineActionsForSinglePage(section, it.index) })
		}
		return result
	}

	private fun determineActionsForSinglePage(section: PageSection, indexInSection: Int): List<PageAction> {
		return if (indexInSection == section.pageCount - 1 && !section.isComplete) {
			// last page in the section; only shows "next" action if section is completed
			listOf(PageAction.NAVIGATE_BACK)
		} else {
			// Either section is complete or there are more pages in the section, so "next" action is shown
			listOf(PageAction.NAVIGATE_BACK, PageAction.NAVIGATE_NEXT)
		}
	}

	private fun subscribeToSections(sections: List<PageSection>) {
		for (sectionItem in sections.withIndex()) {
			val section = sectionItem.value
			sectionsDisposable.addAll(
					section.pageAdditions.subscribe { onPageAdded(it + offsets[sectionItem.index], sectionItem.index, section.pages[it]) },
					section.pageRemovals.subscribe { onPageRemoved(it + offsets[sectionItem.index], sectionItem.index) },
					section.changes.subscribe { onSectionChanged(it) })
		}
	}

	private fun onPageRemoved(index: Int, sectionIndex: Int) {
		for (i in sectionIndex + 1 until offsets.size) {
			offsets[i] -= 1
		}
		// Remove the actions for this page and the page itself
		pageActions.removeAt(index)
		pages.removeAt(index)

		internalPageRemovals.onNext(index)
	}

	private fun onPageAdded(index: Int, sectionIndex: Int, page: Page) {
		for (i in sectionIndex + 1 until offsets.size) {
			offsets[i] += 1
		}
		// Add placeholder actions until actions are determined
		pageActions.addOrInsert(index, emptyList())
		// Add the page
		pages.addOrInsert(index, page)

		internalPageAdditions.onNext(index)
	}

	private fun onSectionChanged(section: PageSection) {
		for (i in 0 until section.pageCount) {
			pageActions[i] = determineActionsForSinglePage(section, i)
		}
	}

	private fun getUpdatedPages(sections: List<PageSection>): List<Page> {
		return sections.flatMap { it.pages }
	}

	private fun getUpdatedLoadingState(sections: List<PageSection>): Boolean {
		// If a section with no pages is loading, show loading for the entire pager
		return sections.find { it.showLoading && it.pageCount == 0 } != null
	}

	private fun calculateOffsets(sections: List<PageSection>, startingOffSet: Int = 0): MutableList<Int> {
		var offset = startingOffSet
		val result = LinkedList<Int>()
		for (section in sections) {
			result.add(offset)
			offset += section.pageCount
		}
		return result
	}

}
