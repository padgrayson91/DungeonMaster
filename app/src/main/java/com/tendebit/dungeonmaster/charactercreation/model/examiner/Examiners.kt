package com.tendebit.dungeonmaster.charactercreation.model.examiner

import com.tendebit.dungeonmaster.charactercreation.model.DndCharacterCreationState
import com.tendebit.dungeonmaster.charactercreation.model.fulfillment.DndClassFulfillment
import com.tendebit.dungeonmaster.charactercreation.model.fulfillment.DndClassOptionsFulfillment
import com.tendebit.dungeonmaster.charactercreation.model.fulfillment.DndProficiencyFulfillment
import com.tendebit.dungeonmaster.charactercreation.model.fulfillment.DndProficiencyOptionsFulfillment
import com.tendebit.dungeonmaster.charactercreation.model.fulfillment.DndRaceFulfillment
import com.tendebit.dungeonmaster.charactercreation.model.fulfillment.DndRaceOptionsFulfillment
import com.tendebit.dungeonmaster.charactercreation.model.fulfillment.Fulfillment
import com.tendebit.dungeonmaster.charactercreation.model.requirement.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.model.requirement.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.model.requirement.DndProficiencyOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.model.requirement.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation.model.requirement.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.model.requirement.DndRaceRequirement
import com.tendebit.dungeonmaster.charactercreation.model.requirement.Requirement

// FIXME: These examiners blindly assume that current selections are valid so long as pre-requisites are met, but this is not necessarily the case
// FIXME: For example, if the list of available classes changes to no longer include the user's selection, that requirement should be recreated with no value

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
				// requirement that options selected in the group display as selected
				fulfillmentList.add(DndProficiencyFulfillment(DndProficiencyRequirement(selectedOption, group)))
			}

			for (selectedOption in state.character.proficiencies.filter { it in group.availableOptions }.filter { it !in group.selectedOptions }) {
				// requirement that an option from this group which was selected for a different group display as selected
				fulfillmentList.add(DndProficiencyFulfillment(DndProficiencyRequirement(selectedOption, group)))
			}

			for (i in 0 until group.remainingChoices()) {
				// requirement for the remaining choices that a value be provided
				fulfillmentList.add(DndProficiencyFulfillment(DndProficiencyRequirement(null, group)))
			}
		}

		// If any proficiency selection is not complete, further requirements should not be queried
		return Examination(fulfillmentList, fulfillmentList.isNotEmpty() && fulfillmentList.find { it.requirement.status == Requirement.Status.NOT_FULFILLED } != null)
	}

}
