package com.tendebit.dungeonmaster.charactercreation3

import com.tendebit.dungeonmaster.charactercreation3.characterclass.DndCharacterClass
import com.tendebit.dungeonmaster.charactercreation3.proficiency.DndProficiencySelection
import com.tendebit.dungeonmaster.charactercreation3.proficiency.ProficiencyProvider
import io.reactivex.subjects.PublishSubject

class CharacterCreation : ProficiencyProvider {

	val character: DndCharacter
	var proficiencyState: ItemState<out DndProficiencySelection> = Removed

	override val proficiencyOptions = PublishSubject.create<ItemState<out DndProficiencySelection>>()

	constructor() {
		character = DndCharacter()
	}

	constructor(character: DndCharacter) {
		this.character = character
	}

	private fun emitAllStates() {
		proficiencyOptions.onNext(proficiencyState)
	}

	fun selectRace(race: Any?) {
		TODO()
	}

	fun selectClass(dndClass: DndCharacterClass?) {
		doLoadClassDetails(dndClass)
		// TODO
	}

	private fun doLoadClassDetails(dndClass: DndCharacterClass?) {
		val newProficiencyState = if (dndClass == null || character.race == null) {
			Removed // Don't expose proficiency options until race and class are selected
		} else {
			Undefined // It isn't possible to know what proficiencies (if any) can be selected until network call completes
		}
		updateProficiencyState(newProficiencyState)
	}

	private fun doLoadRaceDetails() {

	}

	private fun updateProficiencyState(newState: ItemState<out DndProficiencySelection>) {
		proficiencyState = newState
		proficiencyOptions.onNext(proficiencyState)
	}

}
