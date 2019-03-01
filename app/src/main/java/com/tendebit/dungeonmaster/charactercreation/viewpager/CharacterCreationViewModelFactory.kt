package com.tendebit.dungeonmaster.charactercreation.viewpager

import com.tendebit.dungeonmaster.charactercreation.SelectionViewModel
import com.tendebit.dungeonmaster.charactercreation.feature.DndClass
import com.tendebit.dungeonmaster.charactercreation.feature.DndRace
import com.tendebit.dungeonmaster.charactercreation.feature.DndClassOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.DndClassRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.DndRaceOptionsRequirement
import com.tendebit.dungeonmaster.charactercreation.feature.DndRaceRequirement
import com.tendebit.dungeonmaster.core.blueprint.requirement.Requirement

class CharacterCreationViewModelFactory: ViewModelFactory {

	private val classSelection = SelectionViewModel<DndClass>(CLASS_SELECTION)
	private val raceSelection = SelectionViewModel<DndRace>(RACE_SELECTION)

	override fun viewModelFor(requirement: Requirement<*>): ViewModel? {
		if (requirement is DndClassRequirement) {
			classSelection.selectionRequirement = requirement
			return classSelection
		}

		if (requirement is DndClassOptionsRequirement) {
			return classSelection
		}

		if (requirement is DndRaceRequirement) {
			raceSelection.selectionRequirement = requirement
			return raceSelection
		}

		if (requirement is DndRaceOptionsRequirement) {
			return raceSelection
		}

		// TODO: handle proficiencies
		return null
	}

}
