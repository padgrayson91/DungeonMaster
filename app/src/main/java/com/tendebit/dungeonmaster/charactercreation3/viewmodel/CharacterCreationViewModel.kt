package com.tendebit.dungeonmaster.charactercreation3.viewmodel

import com.tendebit.dungeonmaster.charactercreation3.CharacterCreation
import com.tendebit.dungeonmaster.charactercreation3.characterclass.viewmodel.DndCharacterClassSelectionViewModel

class CharacterCreationViewModel(val state: CharacterCreation) {

	val sectionsViewModel = CharacterCreationSectionsViewModel(
			listOf(DndCharacterClassSelectionViewModel(state.classes)))

}