package com.tendebit.dungeonmaster.charactercreation3.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.CharacterCreation
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassSelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.proficiency.ProficiencyPrerequisites
import com.tendebit.dungeonmaster.charactercreation3.proficiency.viewmodel.DndProficiencySelectionViewModel
import com.tendebit.dungeonmaster.charactercreation3.race.viewmodel.DndRaceSelectionViewModel
import com.tendebit.dungeonmaster.core.concurrency.CoroutineConcurrency
import com.tendebit.dungeonmaster.core.viewmodel3.Clearable
import com.tendebit.dungeonmaster.core.viewmodel3.ViewModel
import io.reactivex.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class CharacterCreationViewModel(val state: CharacterCreation) : ViewModel, Clearable {

	private val job = Job()
	private val viewModelScope = CoroutineScope(Dispatchers.Main + job)
	private val concurrency = CoroutineConcurrency(viewModelScope)

	val sectionsViewModel = CharacterCreationSectionsViewModel(
			listOf(DndRaceSelectionViewModel(state.races),
					DndCharacterClassSelectionViewModel(state.classes, concurrency),
					DndProficiencySelectionViewModel(state.proficiencies)))

	override val changes: Observable<out ViewModel>
		get() = Observable.just(this)

	init {
		viewModelScope.launch(context = Dispatchers.IO) {
			state.races.start(concurrency)
			state.classes.start(concurrency)
			state.proficiencies.start(ProficiencyPrerequisites.Impl(state.classes.externalStateChanges.mergeWith(state.classes.internalStateChanges)), viewModelScope)
		}
	}

	override fun clear() {
		job.cancel()
	}

}
