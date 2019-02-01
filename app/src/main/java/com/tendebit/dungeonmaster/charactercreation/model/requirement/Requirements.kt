package com.tendebit.dungeonmaster.charactercreation.model.requirement

import com.tendebit.dungeonmaster.charactercreation.model.DndClass
import com.tendebit.dungeonmaster.charactercreation.model.DndProficiency
import com.tendebit.dungeonmaster.charactercreation.model.DndProficiencyGroup
import com.tendebit.dungeonmaster.charactercreation.pages.raceselection.model.CharacterRaceDirectory

class DndClassRequirement(initialValue: DndClass?, val choices: List<DndClass>): SimpleRequirement<DndClass>(initialValue)

class DndClassOptionsRequirement(initialValue: List<DndClass>): ListRequirement<DndClass>(initialValue)

class DndRaceOptionsRequirement(initialValue: List<CharacterRaceDirectory>): ListRequirement<CharacterRaceDirectory>(initialValue)

class DndRaceRequirement(initialValue: CharacterRaceDirectory?, val choices: List<CharacterRaceDirectory>): SimpleRequirement<CharacterRaceDirectory>(initialValue)

class DndProficiencyOptionsRequirement(initialValue: List<DndProficiencyGroup>): SimpleRequirement<List<DndProficiencyGroup>>(initialValue)

class DndProficiencyRequirement(initialValue: DndProficiency?, val fromGroup: DndProficiencyGroup): SimpleRequirement<DndProficiency>(initialValue) {

	override fun isItemValid(item: DndProficiency): Boolean = super.isItemValid(item) && fromGroup.availableOptions.contains(item)

	/**
	 * [onRevoke] for elements of this type is a NOOP. Data about the revoked [DndProficiency] is retained so that observers can easily determine
	 * which item was deselected
	 */
	override fun onRevoke() {}


}
