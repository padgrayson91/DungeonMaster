package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiency
import com.tendebit.dungeonmaster.core.model.Normal

object CharacterCreationRobots {

	val standardClassListV2 = listOf(
			DndCharacterClass("Barbarian", "example.com/barbarian"),
			DndCharacterClass("Wizard", "example.com/wizard"),
			DndCharacterClass("Fighter", "example.com/fighter"),
			DndCharacterClass("Rogue", "example.com/rogue"))
	val standardProficiencyList = listOf(
			DndProficiency("Stealth", "example.com/stealth"),
			DndProficiency("Burglar's Tools", "example.com/burglar"),
			DndProficiency("Acrobatics", "example.com/acrobatics"),
			DndProficiency("Athletics", "example.com/athletics"))
	val alternateProficiencyList = listOf(DndProficiency("Brewers Supplies", "example.com/brewers+supplies"))

	val blankProficiencyStateList = standardProficiencyList.map { Normal(it) }
	val blankClassStateList = standardClassListV2.map { Normal(it) }

}