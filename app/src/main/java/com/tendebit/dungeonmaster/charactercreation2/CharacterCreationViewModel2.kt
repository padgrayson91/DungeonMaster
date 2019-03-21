package com.tendebit.dungeonmaster.charactercreation2

import com.tendebit.dungeonmaster.charactercreation2.feature.DndCharacter
import com.tendebit.dungeonmaster.charactercreation2.pager.Page
import com.tendebit.dungeonmaster.core.Id
import com.tendebit.dungeonmaster.core.blueprint.Blueprint
import com.tendebit.dungeonmaster.core.blueprint.Delta
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject

class CharacterCreationViewModel2(blueprint: Blueprint<DndCharacter>): ViewModel {

	enum class PageAction {
		NAVIGATE_BACK,
		NAVIGATE_FORWARD
	}

	override val id = Id(CharacterCreationViewModel2::class.java.name)

	// Private Subjects for publishing data
	private val internalLoadingChanges = BehaviorSubject.create<Boolean>()
	private val internalPageChanges = PublishSubject.create<List<Delta<Page>>>()

	// Public Observables
	val loadingChanges = internalLoadingChanges as Observable<Boolean>
	val pageChanges = internalPageChanges as Observable<List<Delta<Page>>>

	// Private data at rest
	private var internalLoading = true

	// Public data at rest
	val pages = LinkedHashMap<Id, Page>()

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

	fun destroy() {
		mainDisposable.dispose()
	}

	fun getActionsForPage(id: Id): Collection<PageAction> {
		TODO()
	}

	private fun processRequirements(requirements: List<Delta<Requirement<*>>>) {
		// TODO: update pages per changes to requirements
	}

}
