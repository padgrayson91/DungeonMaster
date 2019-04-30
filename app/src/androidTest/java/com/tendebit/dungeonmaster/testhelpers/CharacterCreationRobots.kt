package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiency
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass

object CharacterCreationViewRobots {

	val standardProficiencyList = listOf(
			DndProficiency("Stealth", "example.com/stealth"),
			DndProficiency("Burglar's Tools", "example.com/burglar"),
			DndProficiency("Acrobatics", "example.com/acro"),
			DndProficiency("Athletics", "example.com/athletics"))
	val alternateProficiencyList = listOf(DndProficiency("Brewers Supplies", "example.com/brewers+supplies"))

	val blankProficiencyStateList = standardProficiencyList.map { Normal(it) }

	val standardClassList = listOf(
			DndCharacterClass("Wizard", "example.com/wizard"),
			DndCharacterClass("Barbarian", "example.com/barbarian"),
			DndCharacterClass("Rogue", "example.com/rogue"),
			DndCharacterClass("Monk", "example.com/monk"))

}