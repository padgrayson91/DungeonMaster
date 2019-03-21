package com.tendebit.dungeonmaster.charactercreation2.feature

import com.tendebit.dungeonmaster.core.blueprint.requirement.ListRequirement
import com.tendebit.dungeonmaster.core.blueprint.requirement.SelectionRequirement
import com.tendebit.dungeonmaster.core.blueprint.requirement.SimpleRequirement

class DndClassRequirement(initialValue: DndClass?, choices: List<DndClass>): SelectionRequirement<DndClass>(choices, initialValue)

class DndClassOptionsRequirement(initialValue: List<DndClass>): ListRequirement<DndClass>(initialValue)

class DndRaceOptionsRequirement(initialValue: List<DndRace>): ListRequirement<DndRace>(initialValue)

class DndRaceRequirement(initialValue: DndRace?, choices: List<DndRace>): SelectionRequirement<DndRace>(choices, initialValue)

class DndProficiencyOptionsRequirement(initialValue: List<DndProficiencyGroup>): SimpleRequirement<List<DndProficiencyGroup>>(initialValue)

class DndProficiencyRequirement(initialValue: DndProficiencySelection?, val fromGroup: DndProficiencyGroup): SimpleRequirement<DndProficiencySelection>(initialValue) {

	override fun isItemValid(item: DndProficiencySelection): Boolean {
		return super.isItemValid(item) && item.group == fromGroup && fromGroup.availableOptions.contains(item.proficiency)
	}

	/**
	 * [onRevoke] for elements of this type is a NOOP. Data about the revoked [DndProficiency] is retained so that observers can easily determine
	 * which item was deselected
	 */
	override fun onRevoke() {}

}
