package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.charactercreation3.ability.DndAbilitySlot
import com.tendebit.dungeonmaster.charactercreation3.ability.DndAbilityType
import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiency
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
	val emptyAbilitySlotStateList = arrayOf<ItemState<out DndAbilitySlot>>(
			Normal(DndAbilitySlot(Removed, DndAbilityType.STR, 1)),
			Normal(DndAbilitySlot(Removed, DndAbilityType.DEX, 2)),
			Normal(DndAbilitySlot(Removed, DndAbilityType.CON, 1)),
			Normal(DndAbilitySlot(Removed, DndAbilityType.INT, -2)),
			Normal(DndAbilitySlot(Removed, DndAbilityType.WIS, 0)),
			Normal(DndAbilitySlot(Removed, DndAbilityType.CHA, 0)))

	val blankProficiencyStateList = standardProficiencyList.map { Normal(it) }
	val blankClassStateList = standardClassListV2.map { Normal(it) }

}