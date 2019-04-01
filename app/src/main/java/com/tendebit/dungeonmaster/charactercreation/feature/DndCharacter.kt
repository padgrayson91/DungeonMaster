package com.tendebit.dungeonmaster.charactercreation.feature

class DndCharacter {

	var name: String? = null
	var race: DndRace? = null
	var characterClass: DndClass? = null
	val proficiencies = ArrayList<DndProficiencySelection>()

	fun clear() {
		name = null
		race = null
		characterClass = null
		proficiencies.clear()
	}

	override fun toString(): String {
		return "Character named $name is a(n) ${race?.name} ${characterClass?.name} with proficiencies in $proficiencies"
	}
}
