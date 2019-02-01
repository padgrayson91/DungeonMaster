package com.tendebit.dungeonmaster.charactercreation.model

import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory

class DndCharacterCreationState {
	val character = DndCharacter()
	var classOptions = ArrayList<DndClass>()
	val raceOptions = ArrayList<CharacterRaceDirectory>()
	val proficiencyOptions = ArrayList<DndProficiencyGroup>()

}