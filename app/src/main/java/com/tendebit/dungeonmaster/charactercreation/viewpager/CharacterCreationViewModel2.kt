package com.tendebit.dungeonmaster.charactercreation.viewpager

import com.tendebit.dungeonmaster.charactercreation.feature.DndCharacterBlueprint
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject

class CharacterCreationViewModel2(private val blueprint: DndCharacterBlueprint, private val childViewModelFactory: ViewModelFactory,
								  private val pageCollection: ViewModelPageObservableCollection): ViewModel, ViewModelPageObservableCollection by pageCollection {

	enum class PageAction {
		NAVIGATE_BACK,
		NAVIGATE_FORWARD
	}

	override val id = CharacterCreationViewModel2::class.java.name

	// Private Subjects for publishing data
	private val internalLoadingChanges = BehaviorSubject.create<Boolean>()

	// Public Observables
	val loadingChanges = internalLoadingChanges as Observable<Boolean>

	// Private data at rest
	private var internalLoading = true


	var isLoading
		get() = internalLoading
		private set(value) { internalLoading = value; internalLoadingChanges.onNext(value) }
	private var mainDisposable = CompositeDisposable()


	init {
		mainDisposable.add(blueprint.requirements
				.subscribe {
					processRequirements(it)
				})
		internalLoadingChanges.onNext(isLoading)
	}

	override fun clear() {
		blueprint.clear()
		pageCollection.clear()
	}

	fun destroy() {
		pageCollection.clear()
		blueprint.destroy()
		mainDisposable.dispose()
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
			val pageForRequirement = childViewModelFactory.viewModelFor(requirement)

			if (pageForRequirement != null) {
				val oldPageAtIndex = pageCollection.getOrNull(pageIndex)
				if (oldPageAtIndex != pageForRequirement) {
					val indexOfDesiredPage = pageCollection.indexOf(pageForRequirement)
					if (indexOfDesiredPage == -1) {
						// Desired page is not present, need to add it
						pageCollection.insertPage(pageForRequirement, pageIndex)
						pageIndex++
					} else if (indexOfDesiredPage > pageIndex) {
						// Desired page is present, but there are 1 or more stale pages in between
						pageCollection.removePages(pageIndex until indexOfDesiredPage)
						pageIndex++
					}
				}
			}

			requirementIndex++
		}

		// Got to the end of the requirements but still have leftover pages; drop them
		if (pageIndex < pages.size) {
			pageCollection.removePages(pageIndex until pages.size)
		}
	}

}
