package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.charactercreation.feature.DndCharacterBlueprint
import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiency
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.feature.DndRace
import com.tendebit.dungeonmaster.charactercreation.feature.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencyOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation.feature.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.DndRaceRequirement
import com.tendebit.dungeonmaster.charactercreation3.Normal
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

object CharacterCreationViewRobots {

	val standardClassList = listOf(DndClass("Barbarian", "example.com/barbarian"))
	val standardRaceList = listOf(DndRace("Orc", "example.com/orc"))
	val standardProficiencyList = listOf(
			DndProficiency("Stealth", "example.com/stealth"),
			DndProficiency("Burglar's Tools", "example.com/burglar"),
			DndProficiency("Acrobatics", "example.com/acro"),
			DndProficiency("Athletics", "example.com/athletics"))
	val alternateProficiencyList = listOf(DndProficiency("Brewers Supplies", "example.com/brewers+supplies"))

	val blankProficiencyStateList = standardProficiencyList.map { Normal(it) }

	val standardProficiencyGroupList = listOf(DndProficiencyGroup(standardProficiencyList, arrayListOf(), 1),
			DndProficiencyGroup(alternateProficiencyList, arrayListOf(), 1))

}