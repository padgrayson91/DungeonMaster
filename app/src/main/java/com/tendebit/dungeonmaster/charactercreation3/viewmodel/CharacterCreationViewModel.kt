package com.tendebit.dungeonmaster.charactercreation3.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.CharacterCreation
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassSelectionViewModel
import com.tendebit.dungeonmaster.core.viewmodel3.ViewModel
import io.reactivex.Observable

class CharacterCreationViewModel(val state: CharacterCreation) : ViewModel {

	val sectionsViewModel = CharacterCreationSectionsViewModel(
			listOf(DndCharacterClassSelectionViewModel(state.classes)))

	override val changes: Observable<out ViewModel>
		get() = Observable.just(this)

}
