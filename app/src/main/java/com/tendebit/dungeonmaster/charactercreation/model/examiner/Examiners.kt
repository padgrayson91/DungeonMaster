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

abstract class DndCharacterCreationExaminer: BaseExaminer<DndCharacterCreationState>()

class CharacterPrerequisiteExaminer: DndCharacterCreationExaminer() {

	override fun getFulfillmentsForState(state: DndCharacterCreationState): List<Fulfillment<*, DndCharacterCreationState>> {
		val requirements = ArrayList<Fulfillment<*, DndCharacterCreationState>>()
		requirements.add(DndClassOptionsFulfillment(DndClassOptionsRequirement(state.classOptions)))
		requirements.add(DndRaceOptionsFulfillment(DndRaceOptionsRequirement(state.raceOptions)))
		return requirements
	}

}

class CharacterClassExaminer: DndCharacterCreationExaminer() {

	override fun getFulfillmentsForState(state: DndCharacterCreationState): List<Fulfillment<*, DndCharacterCreationState>> {
		val availableClasses = state.classOptions
		return listOf(DndClassFulfillment(DndClassRequirement(state.character.characterClass, availableClasses)))
	}

}

class CharacterRaceExaminer: DndCharacterCreationExaminer() {

	override fun getFulfillmentsForState(state: DndCharacterCreationState): List<Fulfillment<*, DndCharacterCreationState>> {
		if (state.raceOptions.isEmpty()) return emptyList()
		return listOf(DndRaceFulfillment(DndRaceRequirement(state.character.race, state.raceOptions)))
	}

}

class CharacterProficiencyOptionsExaminer: DndCharacterCreationExaminer() {

	override fun getFulfillmentsForState(state: DndCharacterCreationState): List<Fulfillment<*, DndCharacterCreationState>> {
		state.character.characterClass ?: return emptyList()
		return listOf(DndProficiencyOptionsFulfillment(DndProficiencyOptionsRequirement(state.proficiencyOptions)))
	}

}

class CharacterProficiencyExaminer: DndCharacterCreationExaminer() {

	override fun getFulfillmentsForState(state: DndCharacterCreationState): List<Fulfillment<*, DndCharacterCreationState>> {
		state.character.characterClass ?: return emptyList()
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

		return fulfillmentList
	}

}
