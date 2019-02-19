package com.tendebit.dungeonmaster.charactercreation.viewpager

import com.tendebit.dungeonmaster.charactercreation.feature.DndCharacterBlueprint
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.LinkedList
import java.util.concurrent.TimeUnit

class CharacterCreationViewModel2(private val blueprint: DndCharacterBlueprint, private val pageFactory: PageFactory) {

	enum class PageType {
		CLASS_SELECTION,
		RACE_SELECTION,
		PROFICIENCY_SELECTION
	}

	enum class PageAction {
		NAVIGATE_BACK,
		NAVIGATE_FORWARD
	}

	interface PageFactory {

		fun pageFor(requirement: Requirement<*>): Page?

		fun applyData(pageid: String, viewModel: Any)

	}

	interface PageChange {
		val page: Page
	}

	data class Page(val type: PageType, val id: String)
	data class PageInsertion(override val page: Page, val index: Int): PageChange
	data class PageRemoval(override val page: Page): PageChange

	// Private Subjects for publishing data
	private val pageChanges = PublishSubject.create<PageChange>()
	private val internalLoadingChanges = BehaviorSubject.create<Boolean>()

	// Public Observables
	val loadingChanges = internalLoadingChanges as Observable<Boolean>
	val pageAdditions: Observable<PageInsertion> = pageChanges.ofType(PageInsertion::class.java)
	val pageRemovals: Observable<PageRemoval> = pageChanges.ofType(PageRemoval::class.java)

	// Private data at rest
	private var internalLoading = true


	val pages = LinkedList<Page>()
	var isLoading
		get() = internalLoading
		private set(value) { internalLoading = value; internalLoadingChanges.onNext(value) }
	private var mainDisposable = CompositeDisposable()
	val children = HashMap<String, Any>()


	init {
		mainDisposable.add(blueprint.requirements.debounce(10, TimeUnit.MILLISECONDS)
				.subscribe {
					processRequirements(it)
				})
		internalLoadingChanges.onNext(isLoading)
	}

	fun clear() {
		blueprint.clear()
		children.clear()
	}

	fun destroy() {
		children.clear()
		blueprint.destroy()
		mainDisposable.dispose()
	}

	fun handleChildCreated(child: Any, id: String) {
		children[id] = child
		pageFactory.applyData(id, child)
	}

	fun getActionsForPage(id: String): Collection<PageAction> {
		TODO(id)
	}

	private fun processRequirements(requirements: List<Requirement<*>>) {
		// FIXME: there is almost certainly a pretty kotlin/RxJava way to do this

		var requirementIndex = 0
		var pageIndex = 0
		// Send requirements to pageFactory to figure out what pages need to be inserted/removed
		while (requirementIndex < requirements.size) {
			val requirement = requirements[requirementIndex]
			val outOfPages = pageIndex >= pages.size
			val pageAtIndex = if (!outOfPages) pages[pageIndex] else null
			val pageForRequirement = pageFactory.pageFor(requirement)

			if (pageForRequirement != null) {
				if (pageAtIndex == pageForRequirement) {
					// Check if ViewModel is already around
					val existingViewModel = children[pageAtIndex.id]
					if (existingViewModel != null) {
						pageFactory.applyData(pageAtIndex.id, existingViewModel)
					}
				} else if (pages.contains(pageForRequirement)) {
					val indexOfTargetPage = pages.indexOf(pageForRequirement)
					if (indexOfTargetPage > pageIndex) {
						// We had this page already, but some pages in between weren't needed, drop them
						for (page in pages.subList(pageIndex, indexOfTargetPage)) {
							pageChanges.onNext(PageRemoval(page))
						}

						pages.subList(pageIndex, indexOfTargetPage).clear()
					}

					val existingViewModel = children[pageForRequirement.id]
					if (existingViewModel != null) {
						pageFactory.applyData(pageForRequirement.id, existingViewModel)
					}
				} else {
					// This page wasn't present at all, need to insert it
					pageChanges.onNext(PageInsertion(pageForRequirement, pageIndex))
					if (outOfPages) pages.add(pageForRequirement) else pages.add(pageIndex, pageForRequirement)
				}

				pageIndex++
			}

			requirementIndex++
		}

		// Got to the end of the requirements but still have leftover pages; drop them
		if (pageIndex < pages.size) {
			for (page in pages.subList(pageIndex, pages.size)) {
				pageChanges.onNext(PageRemoval(page))
			}

			pages.subList(pageIndex, pages.size).clear()
		}
	}

}
