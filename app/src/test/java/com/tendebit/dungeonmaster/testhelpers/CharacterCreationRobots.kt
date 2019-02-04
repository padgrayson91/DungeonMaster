package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation.feature.DndRace
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement

object CharacterCreationRobots {

	@Suppress("UNCHECKED_CAST")
	fun <T> runRobotForRequirement(requirement: Requirement<T>, testingLevel: ValueRobot.TestingLevel) {
		val robot = when(requirement) {
			is DndRaceOptionsRequirement -> SimpleRobot(listOf(DndRace("Orc", "example.com/orc")))
			is DndClassOptionsRequirement -> SimpleRobot(listOf(DndClass("Barbarian", "example.com/barbarian")))
			else -> null
		} as? ValueRobot<T>
		if (robot != null) {
			requirement.update(robot.getItem(testingLevel))
		}
	}

}