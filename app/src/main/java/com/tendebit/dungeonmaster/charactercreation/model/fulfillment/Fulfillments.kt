package com.tendebit.dungeonmaster.charactercreation.model.fulfillment

import com.tendebit.dungeonmaster.charactercreation.model.DndCharacterCreationState
import com.tendebit.dungeonmaster.charactercreation.model.DndProficiency
import com.tendebit.dungeonmaster.charactercreation.model.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.model.requirement.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation.model.requirement.Requirement
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassDirectory
import com.tendebit.dungeonmaster.charactercreation.pages.classselection.model.CharacterClassManifest
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory

abstract class DndCharacterFulfillment<T>(requirement: Requirement<T>): BaseFulfillment<T, DndCharacterCreationState>(requirement)

class DndClassFulfillment(requirement: Requirement<CharacterClassDirectory>): DndCharacterFulfillment<CharacterClassDirectory>(requirement) {

	override fun applyToState(state: DndCharacterCreationState): Boolean {
		if (state.character.characterClass == requirement.item) return false
		state.character.characterClass = requirement.item
		return true
	}

}

class DndClassOptionsFulfillment(requirement: Requirement<CharacterClassManifest>): DndCharacterFulfillment<CharacterClassManifest>(requirement) {

	override fun applyToState(state: DndCharacterCreationState): Boolean {
		if (state.classOptions == requirement.item) return false
		state.classOptions = requirement.item
		return true
	}

}

class DndRaceOptionsFulfillment(requirement: Requirement<Iterable<CharacterRaceDirectory>>): DndCharacterFulfillment<Iterable<CharacterRaceDirectory>>(requirement) {

	override fun applyToState(state: DndCharacterCreationState): Boolean {
		if (state.raceOptions == requirement.item) return false
		state.raceOptions.clear()
		requirement.item?.let {
			state.raceOptions.addAll(it)
		}
		return true
	}

}

class DndRaceFulfillment(requirement: Requirement<CharacterRaceDirectory>): DndCharacterFulfillment<CharacterRaceDirectory>(requirement) {

	override fun applyToState(state: DndCharacterCreationState): Boolean {
		if (state.character.race == requirement.item) return false
		state.character.race = requirement.item
		return true
	}

}

class DndProficiencyOptionsFulfillment(requirement: Requirement<List<DndProficiencyGroup>>): DndCharacterFulfillment<List<DndProficiencyGroup>>(requirement) {

	override fun applyToState(state: DndCharacterCreationState): Boolean {
		if (state.proficiencyOptions == requirement.item) return false
		state.proficiencyOptions.clear()
		requirement.item?.let {
			state.proficiencyOptions.addAll(it)
		}
		return true
	}

}

class DndProficiencyFulfillment(override val requirement: DndProficiencyRequirement): DndCharacterFulfillment<DndProficiency>(requirement) {

	override fun applyToState(state: DndCharacterCreationState): Boolean {
			requirement.item?.let { proficiency ->
				if (requirement.status == Requirement.Status.NOT_FULFILLED) {
					requirement.fromGroup.selectedOptions.remove(proficiency)
					// state was updated if we were able to remove this proficiency
					return state.character.proficiencies.remove(proficiency)
				}

				if (requirement.status == Requirement.Status.FULFILLED && !state.character.proficiencies.contains(proficiency)) {
					// state was updated if the proficiency provided was not already in the list
					state.character.proficiencies.add(proficiency)
					return true
				}
			}
			// A null proficiency is meaningless
			return false
	}

}
