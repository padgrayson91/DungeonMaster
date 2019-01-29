package com.tendebit.dungeonmaster.charactercreation.model

import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.proficiencyselection.model.CharacterProficiencyDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory

class DndCharacter {
	var name: String? = null
	var race: CharacterRaceDirectory? = null
	var characterClass: CharacterClassDirectory? = null
	val proficiencies = ArrayList<DndProficiency>()
}