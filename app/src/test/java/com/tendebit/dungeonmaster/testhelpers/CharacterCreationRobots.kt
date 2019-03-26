package com.tendebit.dungeonmaster.testhelpers

import com.tendebit.dungeonmaster.charactercreation2.feature.DndCharacterBlueprint
import com.tendebit.dungeonmaster.charactercreation2.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiency
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation2.feature.DndRace
import com.tendebit.dungeonmaster.charactercreation2.feature.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation2.feature.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencyOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation2.feature.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation2.feature.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation2.feature.DndRaceRequirement
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

object CharacterCreationRobots {

	val standardClassList = listOf(DndClass("Barbarian", "example.com/barbarian"))
	val standardRaceList = listOf(DndRace("Orc", "example.com/orc"))
	val standardProficiencyList = listOf(DndProficiency("Stealth", "example.com/stealth"))
	val alternateProficiencyList = listOf(DndProficiency("Brewers Supplies", "example.com/brewers+supplies"))
	val standardProficiencyGroupList = listOf(DndProficiencyGroup(standardProficiencyList, arrayListOf(), 1),
												DndProficiencyGroup(alternateProficiencyList, arrayListOf(), 1))

	@Suppress("UNCHECKED_CAST")
	fun <T> runRobotForRequirement(requirement: Requirement<T>, testingLevel: ValueRobot.TestingLevel = ValueRobot.TestingLevel.SIMPLE) {
		val robot = when(requirement) {
			is DndRaceOptionsRequirement -> SimpleRobot(standardRaceList)
			is DndClassOptionsRequirement -> SimpleRobot(standardClassList)
			is DndRaceRequirement -> SimpleRobot(standardRaceList[0])
			is DndClassRequirement -> SimpleRobot(standardClassList[0])
			is DndProficiencyOptionsRequirement -> SimpleRobot(standardProficiencyGroupList)
			is DndProficiencyRequirement -> SimpleRobot(DndProficiencySelection(requirement.fromGroup.availableOptions[0], requirement.fromGroup))
			else -> null
		} as? ValueRobot<T>
		if (robot != null) {
			requirement.update(robot.getItem(testingLevel))
		}
	}

	fun runUntil(requirementType: Class<Any>, blueprint: DndCharacterBlueprint) {
		blueprint.requirements.subscribe {
			for (requirement in it) {
				if (requirement::class == requirementType) {
					blueprint.destroy()
					break
				}
				if (requirement.status != Requirement.Status.FULFILLED) {
					CharacterCreationRobots.runRobotForRequirement(requirement, ValueRobot.TestingLevel.STANDARD)
				}
			}
		}
	}

}