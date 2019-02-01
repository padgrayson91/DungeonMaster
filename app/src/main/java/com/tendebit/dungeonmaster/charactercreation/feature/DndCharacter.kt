package com.tendebit.dungeonmaster.charactercreation.feature

class DndCharacter {
	var name: String? = null
	var race: DndRace? = null
	var characterClass: DndClass? = null
	val proficiencies = ArrayList<DndProficiency>()
}