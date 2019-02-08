package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiency
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.feature.DndRace
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndProficiencyOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement

object CharacterCreationRobots {

	@Suppress("UNCHECKED_CAST")
	fun <T> runRobotForRequirement(requirement: Requirement<T>, testingLevel: ValueRobot.TestingLevel) {
		val robot = when(requirement) {
			is DndRaceOptionsRequirement -> SimpleRobot(listOf(DndRace("Orc", "example.com/orc")))
			is DndClassOptionsRequirement -> SimpleRobot(listOf(DndClass("Barbarian", "example.com/barbarian")))
			is DndRaceRequirement -> SimpleRobot(DndRace("Orc", "example.com/orc"))
			is DndClassRequirement -> SimpleRobot(DndClass("Barbarian", "example.com/barbarian"))
			is DndProficiencyOptionsRequirement -> SimpleRobot(listOf(
					DndProficiencyGroup(listOf(DndProficiency("Stealth", "example.com/stealth")), arrayListOf(), 1)
																	 ))
			is DndProficiencyRequirement -> SimpleRobot(requirement.fromGroup.availableOptions[0])
			else -> null
		} as? ValueRobot<T>
		if (robot != null) {
			requirement.update(robot.getItem(testingLevel))
		}
	}

}