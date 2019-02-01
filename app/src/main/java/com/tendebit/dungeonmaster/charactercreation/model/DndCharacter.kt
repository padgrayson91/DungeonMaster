package com.tendebit.dungeonmaster.charactercreation.model

import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory

class DndCharacter {
	var name: String? = null
	var race: CharacterRaceDirectory? = null
	var characterClass: DndClass? = null
	val proficiencies = ArrayList<DndProficiency>()
}