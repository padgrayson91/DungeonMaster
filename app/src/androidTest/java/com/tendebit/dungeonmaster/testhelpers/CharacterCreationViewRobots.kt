package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.charactercreation3.ItemState
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiency
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.race.DndRace

@Suppress("unused")
object CharacterCreationViewRobots {

	val standardProficiencyList = listOf(
			DndProficiency("Stealth", "example.com/stealth"),
			DndProficiency("Burglar's Tools", "example.com/burglar"),
			DndProficiency("Acrobatics", "example.com/acro"),
			DndProficiency("Athletics", "example.com/athletics"))
	private val standardRaceList = listOf(
			DndRace("Dwarf", "example.com/dwarf"),
			DndRace("Halfling", "example.com/halfling"),
			DndRace("Dragonborn", "example.com/dragonborn"),
			DndRace("Half-Elf", "example.com/halfelf"),
			DndRace("Gnome", "example.com/gnome"))
	val alternateProficiencyList = listOf(DndProficiency("Brewers Supplies", "example.com/brewers+supplies"))

	val blankProficiencyStateList = standardProficiencyList.map { Normal(it) }

	val standardClassList = listOf(
			DndCharacterClass("Wizard", "example.com/wizard"),
			DndCharacterClass("Barbarian", "example.com/barbarian"),
			DndCharacterClass("Rogue", "example.com/rogue"),
			DndCharacterClass("Monk", "example.com/monk"))

	val blankClassStateList: List<ItemState<DndCharacterClass>>
		get() = standardClassList.map { Normal(it) }
	val blankRaceStateList: List<ItemState<DndRace>>
		get() = standardRaceList.map { Normal(it) }

}