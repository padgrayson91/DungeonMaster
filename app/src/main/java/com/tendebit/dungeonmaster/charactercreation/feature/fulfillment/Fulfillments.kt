package com.tendebit.dungeonmaster.charactercreation.feature.fulfillment

import com.tendebit.dungeonmaster.charactercreation.feature.DndCharacterCreationState
import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiency
import com.tendebit.dungeonmaster.charactercreation.feature.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.feature.DndRace
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.DndProficiencyRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.requirement.Requirement

abstract class DndCharacterFulfillment<T>(requirement: Requirement<T>): BaseFulfillment<T, DndCharacterCreationState>(requirement)

class DndClassFulfillment(requirement: Requirement<DndClass>): DndCharacterFulfillment<DndClass>(requirement) {

	override fun applyToState(state: DndCharacterCreationState): Boolean {
		if (state.character.characterClass == requirement.item) return false
		state.character.characterClass = requirement.item
		return true
	}

}

class DndClassOptionsFulfillment(requirement: Requirement<List<DndClass>>): DndCharacterFulfillment<List<DndClass>>(requirement) {

	override fun applyToState(state: DndCharacterCreationState): Boolean {
		if (state.classOptions == requirement.item) return false
		state.classOptions.clear()
		requirement.item?.let {
			state.classOptions.addAll(it)
		}
		return true
	}

}

class DndRaceOptionsFulfillment(requirement: Requirement<List<DndRace>>): DndCharacterFulfillment<List<DndRace>>(requirement) {

	override fun applyToState(state: DndCharacterCreationState): Boolean {
		if (state.raceOptions == requirement.item) return false
		state.raceOptions.clear()
		requirement.item?.let {
			state.raceOptions.addAll(it)
		}
		return true
	}

}

class DndRaceFulfillment(requirement: Requirement<DndRace>): DndCharacterFulfillment<DndRace>(requirement) {

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
					requirement.fromGroup.selectedOptions.add(proficiency)
					return true
				}
			}
			// A null proficiency is meaningless
			return false
	}

}
