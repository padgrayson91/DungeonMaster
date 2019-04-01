package com.tendebit.dungeonmaster.charactercreation.feature

class DndCharacterCreationState {

	val character = DndCharacter()
	var classOptions = ArrayList<DndClass>()
	val raceOptions = ArrayList<DndRace>()
	val proficiencySources = HashMap<ProficiencySource, MutableList<DndProficiencyGroup>>()
	val proficiencyOptions
		get() = proficiencySources.values.flatten()

	fun clear() {
		character.clear()
		classOptions.clear()
		raceOptions.clear()
		proficiencySources.clear()
	}

}
