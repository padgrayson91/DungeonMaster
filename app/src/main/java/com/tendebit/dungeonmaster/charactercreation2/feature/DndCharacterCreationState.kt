package com.tendebit.dungeonmaster.charactercreation2.feature

class DndCharacterCreationState {

	val character = DndCharacter()
	var classOptions = ArrayList<DndClass>()
	val raceOptions = ArrayList<DndRace>()
	val proficiencyOptions = ArrayList<DndProficiencyGroup>()

	fun clear() {
		character.clear()
		classOptions.clear()
		raceOptions.clear()
		proficiencyOptions.clear()
	}

}
