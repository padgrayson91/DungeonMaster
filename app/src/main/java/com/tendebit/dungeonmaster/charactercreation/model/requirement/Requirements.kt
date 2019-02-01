package com.tendebit.dungeonmaster.charactercreation.model.requirement

import com.tendebit.dungeonmaster.charactercreation.model.DndClass
import com.tendebit.dungeonmaster.charactercreation.model.DndProficiency
import com.tendebit.dungeonmaster.charactercreation.model.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory

class DndClassRequirement(val choices: List<DndClass>): SimpleRequirement<DndClass>()

class DndClassOptionsRequirement: ListRequirement<DndClass>()

class DndRaceOptionsRequirement: ListRequirement<CharacterRaceDirectory>()

class DndRaceRequirement(val choices: List<CharacterRaceDirectory>): SimpleRequirement<CharacterRaceDirectory>()

class DndProficiencyOptionsRequirement: SimpleRequirement<List<DndProficiencyGroup>>()

class DndProficiencyRequirement(val fromGroup: DndProficiencyGroup): SimpleRequirement<DndProficiency>() {

	override fun isItemValid(item: DndProficiency): Boolean = super.isItemValid(item) && fromGroup.availableOptions.contains(item)

	/**
	 * [onRevoke] for elements of this type is a NOOP. Data about the revoked [DndProficiency] is retained so that observers can easily determine
	 * which item was deselected
	 */
	override fun onRevoke() {}


}
