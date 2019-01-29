package com.tendebit.dungeonmaster.charactercreation.model

import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassManifest
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory

class DndCharacterCreationState {
	val character = DndCharacter()
	var classOptions: CharacterClassManifest? = null
	val raceOptions = ArrayList<CharacterRaceDirectory>()
	val proficiencyOptions = ArrayList<DndProficiencyGroup>()

}