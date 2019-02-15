package com.tendebit.dungeonmaster.charactercreation.feature.examiner

import com.tendebit.dungeonmaster.charactercreation.feature.DndCharacterCreationState
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation.feature.fulfillment.DndClassFulfillment
import com.tendebit.dungeonmaster.charactercreation.feature.fulfillment.DndClassOptionsFulfillment
import com.tendebit.dungeonmaster.charactercreation.feature.fulfillment.DndProficiencyFulfillment
import com.tendebit.dungeonmaster.charactercreation.feature.fulfillment.DndProficiencyOptionsFulfillment
import com.tendebit.dungeonmaster.charactercreation.feature.fulfillment.DndRaceFulfillment
import com.tendebit.dungeonmaster.charactercreation.feature.fulfillment.DndRaceOptionsFulfillment
import com.tendebit.dungeonmaster.charactercreation.feature.fulfillment.Fulfillment
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndProficiencyOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndRaceRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement

// FIXME: These examiners blindly assume that current selections are valid so long as pre-requisites are met, but this is not necessarily the case
// FIXME: For example, if the list of available classes changes to no longer include the user's selection, that selectionRequirement should be recreated with no value

abstract class DndCharacterCreationExaminer: Examiner<DndCharacterCreationState>

class CharacterPrerequisiteExaminer: DndCharacterCreationExaminer() {

	override fun examine(state: DndCharacterCreationState): Examination<DndCharacterCreationState> {
		val requirements = ArrayList<Fulfillment<*, DndCharacterCreationState>>()
		requirements.add(DndClassOptionsFulfillment(DndClassOptionsRequirement(state.classOptions)))
		requirements.add(DndRaceOptionsFulfillment(DndRaceOptionsRequirement(state.raceOptions)))
		return Examination(requirements, requirements.all { it.requirement.status == Requirement.Status.NOT_FULFILLED })
	}

}

class CharacterClassExaminer: DndCharacterCreationExaminer() {

	override fun examine(state: DndCharacterCreationState): Examination<DndCharacterCreationState> {
		val availableClasses = state.classOptions
		return Examination(
				listOf(DndClassFulfillment(DndClassRequirement(state.character.characterClass, availableClasses))),
				availableClasses.isEmpty())
	}

}

class CharacterRaceExaminer: DndCharacterCreationExaminer() {

	override fun examine(state: DndCharacterCreationState): Examination<DndCharacterCreationState> {
		if (state.raceOptions.isEmpty()) return Examination(emptyList(), true)
		return Examination(listOf(DndRaceFulfillment(DndRaceRequirement(state.character.race, state.raceOptions))), state.character.race == null)
	}

}

class CharacterProficiencyOptionsExaminer: DndCharacterCreationExaminer() {

	override fun examine(state: DndCharacterCreationState): Examination<DndCharacterCreationState> {
		state.character.characterClass ?: return Examination(emptyList(), true)
		return Examination(listOf(DndProficiencyOptionsFulfillment(DndProficiencyOptionsRequirement(state.proficiencyOptions))), state.proficiencyOptions.isEmpty())
	}

}

class CharacterProficiencyExaminer: DndCharacterCreationExaminer() {

	override fun examine(state: DndCharacterCreationState): Examination<DndCharacterCreationState> {
		val availableProficiencies = state.proficiencyOptions
		val fulfillmentList = ArrayList<DndProficiencyFulfillment>()
		for (group in availableProficiencies) {
			for (selectedOption in group.selectedOptions) {
				// selectionRequirement that options selected in the group display as selected
				fulfillmentList.add(DndProficiencyFulfillment(DndProficiencyRequirement(DndProficiencySelection(selectedOption, group), group)))
			}

			for (selectedOption in state.character.proficiencies.filter { it.proficiency in group.availableOptions }.filter { it.group != group }) {
				// selectionRequirement that an option from this group which was selected for a different group display as selected
				fulfillmentList.add(DndProficiencyFulfillment(DndProficiencyRequirement(selectedOption, group)))
			}

			for (i in 0 until group.remainingChoices()) {
				// selectionRequirement for the remaining choices that a value be provided
				fulfillmentList.add(DndProficiencyFulfillment(DndProficiencyRequirement(null, group)))
			}
		}

		// If any proficiency selection is not complete, further requirements should not be queried
		return Examination(fulfillmentList, fulfillmentList.isNotEmpty() && fulfillmentList.find { it.requirement.status == Requirement.Status.NOT_FULFILLED } != null)
	}

}
