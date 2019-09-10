package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityBonus
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilitySlot
import com.tendebit.dungeonmaster.charactercreation3.abilitycore.DndAbilityType
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiencycore.DndProficiency
import com.tendebit.dungeonmastercore.model.state.ItemState
import com.tendebit.dungeonmastercore.model.state.Normal
import com.tendebit.dungeonmastercore.model.state.Removed

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
	val defaultBonusList = arrayOf(
			DndAbilityBonus(DndAbilityType.STR, 0),
			DndAbilityBonus(DndAbilityType.DEX, 0),
			DndAbilityBonus(DndAbilityType.CON, 0),
			DndAbilityBonus(DndAbilityType.INT, 0),
			DndAbilityBonus(DndAbilityType.WIS, 0),
			DndAbilityBonus(DndAbilityType.CHA, 0))
	val arbitraryBonusList = arrayOf(
			DndAbilityBonus(DndAbilityType.STR, 1),
			DndAbilityBonus(DndAbilityType.DEX, 2),
			DndAbilityBonus(DndAbilityType.CON, 1),
			DndAbilityBonus(DndAbilityType.INT, -2),
			DndAbilityBonus(DndAbilityType.WIS, 0),
			DndAbilityBonus(DndAbilityType.CHA, 0))
	val arbitraryBonusList2 = arrayOf(
			DndAbilityBonus(DndAbilityType.STR, 0),
			DndAbilityBonus(DndAbilityType.DEX, 0),
			DndAbilityBonus(DndAbilityType.CON, 0),
			DndAbilityBonus(DndAbilityType.INT, 0),
			DndAbilityBonus(DndAbilityType.WIS, 1),
			DndAbilityBonus(DndAbilityType.CHA, 2))
	val emptyAbilitySlotStateList = arrayOf<ItemState<out DndAbilitySlot>>(
			Normal(DndAbilitySlot(Removed, arbitraryBonusList[0])),
			Normal(DndAbilitySlot(Removed, arbitraryBonusList[1])),
			Normal(DndAbilitySlot(Removed, arbitraryBonusList[2])),
			Normal(DndAbilitySlot(Removed, arbitraryBonusList[3])),
			Normal(DndAbilitySlot(Removed, arbitraryBonusList[4])),
			Normal(DndAbilitySlot(Removed, arbitraryBonusList[5])))



	val blankProficiencyStateList = standardProficiencyList.map { Normal(it) }
	val blankClassStateList = standardClassListV2.map { Normal(it) }

}