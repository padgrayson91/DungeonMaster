package com.tendebit.dungeonmaster.charactercreation2

import com.tendebit.dungeonmaster.charactercreation.feature.DndCharacterCreationState
import com.tendebit.dungeonmaster.core.viewmodel2.Page
import com.tendebit.dungeonmaster.charactercreation2.pager.PageAction
import com.tendebit.dungeonmaster.core.Id
import com.tendebit.dungeonmaster.core.blueprint.Delta
import com.tendebit.dungeonmaster.core.blueprint.IBlueprint
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement
import com.tendebit.dungeonmaster.core.viewmodel2.ViewModel
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import java.util.LinkedList

class CharacterCreationViewModel2(blueprint: IBlueprint<DndCharacterCreationState>): ViewModel {

	override val id = Id(CharacterCreationViewModel2::class.java.name)

	// Private Subjects for publishing data
	private val internalLoadingChanges = BehaviorSubject.create<Boolean>()
	private val internalPageChanges = PublishSubject.create<List<Delta<Page>>>()
	private val internalIndexChanges = BehaviorSubject.create<Int>()

	// Public Observables
	val loadingChanges = internalLoadingChanges as Observable<Boolean>
	val pageChanges = internalPageChanges as Observable<List<Delta<Page>>>
	val indexChanges = internalIndexChanges as Observable<Int>

	// Private data at rest
	private var internalLoading = true
	private var index = 0

	// Public data at rest
	val pages = LinkedHashMap<Id, Page>()

	var isLoading
		get() = internalLoading
		private set(value) { internalLoading = value; internalLoadingChanges.onNext(value) }
	private var mainDisposable = CompositeDisposable()


	init {
		mainDisposable.add(blueprint.requirements
				.subscribe {
					processRequirements(LinkedList(it))
				})
		internalLoadingChanges.onNext(isLoading)
	}

	fun destroy() {
		mainDisposable.dispose()
	}

	fun getActionsForPage(id: Id): Collection<PageAction> {
		TODO()
	}

	fun pageChangedByView(newPage: Int) {
		index = newPage
	}

	private fun processRequirements(requirements: MutableList<Delta<Requirement<*>>>) {
		TODO()
	}

}
