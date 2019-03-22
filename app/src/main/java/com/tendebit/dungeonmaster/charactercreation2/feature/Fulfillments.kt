package com.tendebit.dungeonmaster.charactercreation2.feature

import com.tendebit.dungeonmaster.core.blueprint.fulfillment.BaseFulfillment
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

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

class DndProficiencyOptionsFulfillment(requirement: DndProficiencyOptionsRequirement): DndCharacterFulfillment<List<DndProficiencyGroup>>(requirement) {
	private val forSource = requirement.forSource

	override fun applyToState(state: DndCharacterCreationState): Boolean {
		val listToUpdate = state.proficiencySources[forSource] ?: ArrayList()
		if (listToUpdate == requirement.item) return false
		listToUpdate.clear()
		requirement.item?.let {
			listToUpdate.addAll(it)
		}
		state.proficiencySources[forSource] = listToUpdate
		return true
	}

}

class DndProficiencyFulfillment(override val requirement: Requirement<DndProficiencySelection>): DndCharacterFulfillment<DndProficiencySelection>(requirement) {

	override fun applyToState(state: DndCharacterCreationState): Boolean {
			requirement.item?.let { proficiencySelection ->
				if (requirement.status == Requirement.Status.NOT_FULFILLED) {
					proficiencySelection.group.selectedOptions.remove(proficiencySelection.proficiency)
					// state was updated if we were able to remove this proficiency
					return state.character.proficiencies.remove(proficiencySelection)
				}

				if (requirement.status == Requirement.Status.FULFILLED && !state.character.proficiencies.contains(proficiencySelection)) {
					// state was updated if the proficiency provided was not already in the list
					state.character.proficiencies.add(proficiencySelection)
					proficiencySelection.group.selectedOptions.add(proficiencySelection.proficiency)
					return true
				}
			}
			// A null proficiency is meaningless
			return false
	}

}
